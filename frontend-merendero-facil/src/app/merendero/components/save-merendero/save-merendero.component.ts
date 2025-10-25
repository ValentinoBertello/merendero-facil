import { Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../user/services/auth.service';
import { UserService } from '../../../user/services/user.service';
import { LocationPickerComponent } from './location-picker/location-picker.component';
import { MerenderoDetailsFormComponent, MerenderoReactiveFormDto } from './merendero-details-form/merendero-details-form.component';
import { MerenderoService } from '../../services/merendero.service';
import { UbicationDto } from '../../models/ubication.model';


@Component({
  selector: 'app-save-merendero',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule,
    LocationPickerComponent, MerenderoDetailsFormComponent
  ],
  templateUrl: './save-merendero.component.html',
  styleUrl: './save-merendero.component.css'
})
export class SaveMerenderoComponent {
  // Variable para controlar los pasos
  currentStep: number = 1;

  // MAPA Y DIRECCIÓN (nos llega del componente location-picker)
  ubicationDto: UbicationDto = {
    name: "",
    address: "",
    url: "",
    latitude: 0,
    longitude: 0
  }

  // Se ejecuta cuando recibimos la dirección seleccionada
  onLocationSelected(ub: UbicationDto) {
    this.ubicationDto = ub;
    this.currentStep = 2;
  }
 
  // FORMULARIO REACTIVO (nos llega del componente merendero-details-form)
  merenderoFormDto: MerenderoReactiveFormDto = {
    name: "",
    openingTime: "",
    closingTime: "",
    capacity: 0,
    daysOpen: ""
  };

  // Se ejecuta cuando recibimos el merendero creado
  getMerenderoCreated(merendero: MerenderoReactiveFormDto) {
    this.merenderoFormDto = merendero;
    this.currentStep = 3;
  }

  // TERCER PASO (conectar con Mercado Pago y registrar el merendero en la bd)
  constructor(public authService: AuthService) {
  }
  
  private readonly merenderoService = inject(MerenderoService);
  private readonly userService = inject(UserService);

  /**
   * Conecta con Mercado Pago armando el dto con los datos del merendero y 
   * redirigimos al usuario a la URL de autorización
   * **/
  conectarConMercadoPago() {
    const emailActual = this.authService.getCurrentUserEmail();
    // Buscar al usuario en la BD a partir de su email
    this.userService.getUserByEmail(emailActual).subscribe({
      next: (response) => {
        // Construimos el objeto merendero
        const merenderoData = this.buildMerenderoData(response.id, emailActual);
        // Codificamos los datos del merendero para enviarlos en la url
        const encodedState = this.encodeState(merenderoData);
        // Obtenemos la URL de autorización de Mercado Pago
        this.merenderoService.getAuthorizationUrl(encodedState).subscribe({
          next: (response) => {
            // Redirigir al usuario a la página de autorización de Mercado Pago
            window.location.href = response.link;
          },
          error: (err) => console.error('Error al conectar:', err)
        });
      },
      error: (err) => console.error('Error al conectar:', err)
    });
  }

  /**
   * Codifica un objeto a string seguro en Base64-URL para pasarlo como parámetro luego
   * en la url de autorización
   * **/
  private encodeState(data: any): string {
    const jsonState = JSON.stringify(data);
    const base64State = btoa(
      encodeURIComponent(jsonState).replace(/%([0-9A-F]{2})/g, (match, p1) => {
        return String.fromCharCode(parseInt(p1, 16));
      })
    );
    return encodeURIComponent(
      base64State
        .replace(/\+/g, '-')
        .replace(/\//g, '_')
        .replace(/=+$/, '')
    );
  }

  /**
   * Construye el objeto con los datos del merendero a partir del ubicationDto y el merenderoFormDto
   * **/
  private buildMerenderoData(userId: number, emailActual: string) {
    return {
      name: this.merenderoFormDto.name,
      address: this.ubicationDto.address,
      latitude: this.ubicationDto.latitude,
      longitude: this.ubicationDto.longitude,
      capacity: this.merenderoFormDto.capacity,
      daysOpen: this.merenderoFormDto.daysOpen,
      openingTime: this.merenderoFormDto.openingTime,
      closingTime: this.merenderoFormDto.closingTime,
      createdUser: userId,
      managerEmail: emailActual,
    };
  }
}