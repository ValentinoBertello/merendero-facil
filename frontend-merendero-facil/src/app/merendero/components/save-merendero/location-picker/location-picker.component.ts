import { ChangeDetectorRef, Component, ElementRef, EventEmitter, NgZone, Output, ViewChild } from '@angular/core';
import { GoogleMap, GoogleMapsModule, MapAdvancedMarker } from '@angular/google-maps';
import { UbicationDto } from '../../../models/ubication.model';

@Component({
  selector: 'app-location-picker',
  standalone: true,
  imports: [GoogleMap, MapAdvancedMarker, GoogleMapsModule],
  templateUrl: './location-picker.component.html',
  styleUrl: './location-picker.component.css'
})
export class LocationPickerComponent {
  // Elemento a enviar al componente Padre
  ubicationDto: UbicationDto = {
    name: "",
    address: "",
    url: "",
    latitude: 0,
    longitude: 0
  }
  // Evento con la ubicación seleccioanda
  @Output() locationSelected = new EventEmitter<UbicationDto>();
  zoom = 11;
  // property binding, si cambia, cambia de lugar el marker
  markerPosition: google.maps.LatLngLiteral = { lat: -31.41350000, lng: -64.18105000 };
  // coordenada central del mapa
  center = this.markerPosition;
  // Con PlacesService podemos acceder a detalles completos de un lugar con su placeId
  private placesService!: google.maps.places.PlacesService;
  // Con autocomplete podemos agregar campo de texto inteligente que sugiere direcciones.
  // al seleccionar una dirección, retornará un placeId
  private autocomplete: google.maps.places.Autocomplete | undefined;
  // Obtenemos el field de dirección
  @ViewChild('inputAddress') inputAddress!: ElementRef<HTMLInputElement>;
  // Obtenemos el marker
  @ViewChild(MapAdvancedMarker) marker!: MapAdvancedMarker;

  constructor(private ngZone: NgZone, private cdr: ChangeDetectorRef) {
  }

  // Se ejecuta Después que el html cargue y se muestre
  async ngAfterViewInit(): Promise<void> {

    await google.maps.importLibrary("places");
    this.initPlacesService();
    // Hacemos que el inputAddress del html tenga el autocompletado, y lo almacenamos en "this.autocomplete"
    this.autocomplete = new google.maps.places.Autocomplete(this.inputAddress.nativeElement);
    // Manejamos dinámicamente cuando el usuario seleccione un place
    this.handlePlaceSelection();
    // Pedido de ubicación al usuario
    this.getUserLocation();
  }

  /**
   * Pedimos ubicación al usuario para mostrarla en el mapa y marker
   * **/
  getUserLocation(): void {
    // Verificamos si el navegador soporta geolocalización
    if (navigator.geolocation) {
      // Pedimos la ubicación al usuario
      navigator.geolocation.getCurrentPosition(
        // Si el user acepta
        (position) => {
          // Actualizamos mapa con ubi nueva
          this.updateMapPosition(position.coords.latitude, position.coords.longitude);
          this.updateAddressFromCoordinates();
        },
        (error) => {
          console.error('Error getting location:', error);
        },
        { timeout: 10000 }
      );
    } else {
      console.warn('Geolocation is not supported by this browser.');
    }
  }

  /**
   * Traducimos las coordenadas del markerPosition a un placeId
   * **/
  private async updateAddressFromCoordinates() {
    // Importamos librería de geocodificación
    const { Geocoder } = await google.maps.importLibrary("geocoding") as google.maps.GeocodingLibrary;
    // Instanciar de Geocoder. Es un traductor de coordenadas a direcciones.
    const geocoder = new Geocoder();
    try {
      // response devuelve un array de resultados con placeId, etc.
      const response = await geocoder.geocode({
        location: this.markerPosition
      });

      if (response.results[0]) {
        const placeId = response.results[0].place_id;
        this.fetchPlaceDetails(placeId);
      }
    } catch (error) {
      console.error('Error en geocodificación:', error);
    }
  }

  /**
   * Obtenemos información detallada del lugar (dirección, nombre, fotos, etc.) a partir del placeId
   * **/
  private fetchPlaceDetails(placeId: string) {
    // Pedimos información detallada de un lugar por su placeId
    this.placesService.getDetails({
      placeId,
      fields: ['name', 'formatted_address', 'geometry', 'icon', 'url', 'photos']
    }, (place, status) => {
      if (status === 'OK' && place) {
        this.ngZone.run(() => {
          // Obtenemos dirección del lugar y la ponemos en el input Address
          this.inputAddress.nativeElement.value = place.formatted_address!;
          // Guardamos todo en ubicationDto
          this.fillUbicationDto(place);
        });
      }
    });
  }

  /**
   * Método que se ejecuta cuando el user mueva el marker
   * **/
  onMarkerDragEnd(event: google.maps.MapMouseEvent) {
    this.ngZone.run(() => {
      if (event.latLng) {
        // Actualizamos posicion del mapa
        this.updateMapPosition(
          event.latLng.lat(),
          event.latLng.lng()
        );
        this.cdr.detectChanges();
        this.updateAddressFromCoordinates();
      }
    });
  }

  /**
   * Registramos una funcion que se ejecutará cuando el usuario seleccione una sugerencia
   * del autocomplete
   * **/
  private handlePlaceSelection() {
    if (this.autocomplete) this.autocomplete.addListener('place_changed', () => {
      // Con ngZone forzamos la ejecución dentro del Angular Zone
      this.ngZone.run(() => {
        // Obtenemos objeto PlaceResult que trae la info del lugar seleccionado por el usuario
        const place = this.autocomplete?.getPlace();
        if (place?.geometry?.location) {
          const lat = place.geometry!.location!.lat();
          const lng = place.geometry!.location!.lng();

          this.fillUbicationDto(place); // Llenamos el ubicationDto
          this.updateMapPosition(lat, lng); // Actualizamos mapa
        }
      });
    });
  }

  /**
   * Construimos objeto con los datos de la dirección seleccionada
   * en el autocomplete
   * **/
  private fillUbicationDto(place: google.maps.places.PlaceResult) {
    if (place?.geometry?.location) this.ubicationDto = {
      name: place.name ?? "No disponible",
      address: this.inputAddress.nativeElement.value,
      url: place.url ?? `No disponible`,
      latitude: place.geometry.location.lat(),
      longitude: place.geometry.location.lng()
    };
  }

  /**
   * Actualizamos mapa para que vaya al lugar pasado por parámetro
   * **/
  private updateMapPosition(lat: number, lng: number) {
    this.markerPosition = { lat: lat, lng: lng };
    this.center = this.markerPosition;
    this.zoom = 16;
  }

  /**
   * Inicializamos el placesService
   * **/
  private initPlacesService() {
    this.placesService = new google.maps.places.PlacesService(document.createElement('div'));
  }

  /**
   * Emitimos al componente padre la ubi seleccionada por el user
   * **/
  emitUbication() {
    this.locationSelected.emit(this.ubicationDto);
  }
}