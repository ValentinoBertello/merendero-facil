import { ChangeDetectorRef, Component, ElementRef, inject, NgZone, QueryList, viewChild, ViewChildren, viewChildren } from '@angular/core';
import { MerenderoService } from '../../services/merendero.service';
import { GoogleMap, MapAdvancedMarker, MapInfoWindow } from '@angular/google-maps';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../user/services/auth.service';
import Swal from 'sweetalert2';
import { Filters, MerenderoFilterPanelComponent } from './merendero-filter-panel/merendero-filter-panel.component';
import { MerenderosFilterService } from '../../services/merenderos-filter.service';
import { MerenderoInfoPanelComponent } from './merendero-info-panel/merendero-info-panel.component';
import { MerenderoResponseDto } from '../../models/merendero-response.model';
import { AlertService } from '../../../shared/services/alert.service';

@Component({
  selector: 'app-list-merenderos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MapInfoWindow, RouterModule, GoogleMap, MapAdvancedMarker,
    MerenderoFilterPanelComponent, MerenderoInfoPanelComponent
  ],
  templateUrl: './list-merenderos.component.html',
  styleUrl: './list-merenderos.component.css'
})
export class ListMerenderosComponent {
  private readonly alertService = inject(AlertService);
  private readonly merenderoService = inject(MerenderoService);
  private readonly merenderoFilterService = inject(MerenderosFilterService);
  private zone = inject(NgZone);

  // Referencias de Template
  private mapInfoWindow = viewChild.required(MapInfoWindow); // Ventana de info en cada marker
  @ViewChildren('locationCard', { read: ElementRef })
  private locationCards!: QueryList<ElementRef>; // Traemos todas las tarjetas de la izquierda
  private markersRef = viewChildren(MapAdvancedMarker); // Traemos todos los markers

  // Datos para visualización
  filteredMerenderos: MerenderoResponseDto[] = [];
  merenderos: MerenderoResponseDto[] = [];

  // Estado de UI
  isSorting: boolean = false;
  selectedMerendero: MerenderoResponseDto | null = null;
  selectedMerenderoId: number | null = null;
  showInfoPanel: boolean = false;
  showFilters = false;

  // Configuración del Mapa
  center: google.maps.LatLngLiteral = { lat: -31.41350000, lng: -64.18105000 };
  private debounceTimer: any;

  constructor(public authService: AuthService, private router: Router, private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.getUserLocation();
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     Métodos de Navegación y UI
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  irAMiMerendero() {
    this.router.navigate(['/donaciones']);
  }

  abrirInfoMerendero(merendero: MerenderoResponseDto, index: number) {
    this.selectedMerendero = merendero;
    this.showInfoPanel = true;
    setTimeout(() => this.goToPlace(merendero, index), 0);
  }

  cerrarInfoMerendero() {
    this.showInfoPanel = false;
    this.selectedMerendero = null;
  }

  pestaniaNoAut() {
    Swal.fire({
      title: 'Debes loguearte!',
      text: 'Para realizar una donación debes iniciar sesión',
      icon: 'info',
      showCancelButton: true,
      confirmButtonColor: '#e79e36',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Iniciar sesión',
      cancelButtonText: 'Cancelar',
      background: '#f8f9fa',
      iconColor: '#e79e36'
    }).then((result) => {
      if (result.isConfirmed) {
        this.router.navigate(['/login-user']);
      }
    });
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     Métodos del Mapa
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  // Este método se ejecuta cuando el mapa se inicializa
  onMapInitialized(map: google.maps.Map) {
    // Agregamos un listener al evento 'center_changed' que se dispara cuando
    // el centro del mapa cambia.
    map.addListener('idle', () => {
      // Limpia cualquier temporizador previo
      clearTimeout(this.debounceTimer);

      // prender spinner de debounce
      this.zone.run(() => {
        this.isSorting = true;
        this.cdr.detectChanges();
      });

      // Configuramos nuevo temporizador de 700ms
      this.debounceTimer = setTimeout(() => {
        // Obtenemos el centro actual del mapa
        const center = map.getCenter();
        if (center) {
          // Obtiene las coordenadas del centro del mapa
          const currentLat = center.lat();
          const currentLng = center.lng();

          // apagar debounce, prender loading de red y llamar
          this.zone.run(() => {
            this.isSorting = false;
            this.cdr.detectChanges();
          });

          this.loadMerenderosByCoords(currentLat, currentLng);
          this.cdr.detectChanges();
        }
        this.isSorting = false;
        this.cdr.detectChanges();
      }, 750);

    });
  }
  /**
   * Método que abre la ventana de información de un marker específico.
   * Además se selecciona el merendero asociado en la lista de la izquierda.
   * **/
  onMarkerClick(merendero: MerenderoResponseDto, marker: MapAdvancedMarker) {
    // Contenido de la mini ventana
    const content = `

       <div class="info-window-container">
        <div class="header">
          <h4>${merendero.name}</h4>
          <span class="status ${merendero.openNow ? 'open' : 'closed'}">
            ${merendero.openNow ? '🟢 ABIERTO AHORA' : '🔴 CERRADO'}
          </span>
        </div>
        <div class="content">
          <div class="info-item address">
            🏠 <strong>Dirección:</strong>
            <div class="address-text">${merendero.address}</div>
          </div>
            <a
  href="https://www.google.com/maps?q=${merendero.latitude},${merendero.longitude}"
  target="_blank"
  rel="noopener noreferrer"
>
  Ver en Google Maps
</a>
          
          <div class="info-item schedule">
            🕒 <strong>Horario:</strong>
            <div class="time-range">
              ${merendero.openingTime} - ${merendero.closingTime}
              ${merendero.openNow ? '<span class="current-status"></span>' : ''}
            </div>
          </div>
      </div>
    `;

    // el MapInfoWindow tiene el metodo open, para abrir el cartel en el marker indicado
    // con el contenido indicado
    this.mapInfoWindow().open(marker, false, content);

    // Marcar la tarjeta correspondiente como "seleccionada"
    this.selectedMerenderoId = merendero.id;

    // Localizar el índice de ese merendero en la lista filtrada
    const index = this.filteredMerenderos.findIndex(m => m.id === merendero.id);

    // Hacemos scroll suave hasta la tarjeta en la lista
    this.scrollToCard(index);
    console.log("index:" + index);
  }

  /**
   * Método que se activa al tocar merendero en la lista.
   * Te traslada al merendero en el mapa.
   * **/
  goToPlace(location: MerenderoResponseDto, index: number) {
    this.selectedMerenderoId = location.id;
    const markers = this.markersRef();
    const mRef = markers[index];

    this.onMarkerClick(location, mRef);
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     Métodos de Datos (Servicios)
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  /**
  * Obtenemos los merenderos desde el backend, pasando la lat y lgt y el radio de km
  * deseado
  * **/
  private loadMerenderosByCoords(lat: number, lng: number) {
    console.log("lat:  " + lat + "  lng:  " + lng);
    this.merenderoService.getClosestMerenderos(40, lat, lng).subscribe({
      next: (merenderosR) => {
        console.log("merenderosR: " + merenderosR)
        this.merenderos = merenderosR;
        this.filteredMerenderos = [...merenderosR]; // ya vienen ordenados por backend
        this.filterMerenderos();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al obtener merenderos:', err);
        this.alertService.error("Ocurrió un error inesperado");
      }
    });
  }

  /**
   * Pedimos ubicación al user y cargamos los merenderos.
   * **/
  private getUserLocation() {
    this.isSorting = true;
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          this.center = { lat: position.coords.latitude, lng: position.coords.longitude };
          this.loadMerenderosByCoords(position.coords.latitude, position.coords.longitude);
          this.isSorting = false;
        },
        (error) => {
          console.error('Error getting location:', error);
          this.loadMerenderosByCoords(-31.41350000, -64.18105000); // Load with default coordinates
        },
        {
          timeout: 10000,
          enableHighAccuracy: true
        }
      );
    } else {
      this.loadMerenderosByCoords(-31.41350000, -64.18105000);
    }
  }

  // Borrar merendero
  deleteMerendero(id: number) {
    this.merenderoService.deleteMerenderoById(id).subscribe({
      next: () => {
        // Removerlo de la lista local para actualizar la vista
        this.merenderos = this.merenderos.filter(m => m.id !== id);
        this.ngOnInit();
      },
      error: err => {
        console.error(err);
      }
    });
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
       Métodos de Filtrado
    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  filterText: string = '';
  filters = {
    openNow: false,
    capacityMin: null as number | null,
    openEveryDay: false
  };
  filterMerenderos() {
    this.filteredMerenderos = this.merenderoFilterService.filterMerenderos(
      this.merenderos,
      this.filters,
      this.filterText
    );
    this.showFilters = false;
  }

  onApplyFilters(newFilters: Filters) {
    this.filters = newFilters;
    this.filterMerenderos();
    this.showFilters = false;
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
       Métodos de Utilidad
    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  /** Hace scroll suave hasta la tarjeta con índice `index` */
  private scrollToCard(index: number) {
    // Esperamos a que ViewChildren esté actualizado
    setTimeout(() => {
      const cardsArray = this.locationCards.toArray();
      cardsArray[index].nativeElement.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
      console.log("cardsArray" + cardsArray);
    }, 100);

  }

}