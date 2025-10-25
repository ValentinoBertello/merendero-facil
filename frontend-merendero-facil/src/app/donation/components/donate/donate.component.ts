import { Component, inject } from '@angular/core';
import { AuthService } from '../../../user/services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PayRequestDto } from '../../models/pay-request.model';
import { DonationService } from '../../services/donation.service';
import { AlertService } from '../../../shared/services/alert.service';

@Component({
  selector: 'app-donate',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './donate.component.html',
  styleUrl: './donate.component.css'
})
export class DonateComponent {
  // Inyección de Dependencias
  private readonly alertService = inject(AlertService);
  private readonly donationService = inject(DonationService);

  // Propiedades
  isLoading = false;
  private merenderoId = 0;
  showCustomAmount = false;
  formReactivo: FormGroup;

  // Constructor
  constructor(public authService: AuthService, private route: ActivatedRoute) {
    this.formReactivo = new FormGroup({
      amount: new FormControl('', [
        Validators.required,
        Validators.min(100)
      ])
    });
  }

  // Donación directa para montos fijos
  donateFixedAmount(amount: number) {
    this.formReactivo.get('amount')?.setValue(amount);
    this.processDonation();
  }

  // Alternar formulario personalizado y montos fijos
  toggleCustomAmount() {
    this.showCustomAmount = !this.showCustomAmount;
    if (!this.showCustomAmount) {
      this.formReactivo.get('amount')?.reset();
    }
  }

  /**
   * Procesa la donación: arma el request y redirige al link de pago
   **/
  processDonation() {
    this.isLoading = true;
    this.merenderoId = Number(this.route.snapshot.paramMap.get('id'));
    const formValue = this.formReactivo.value;

    // Objeto que mandamos al back para que gestione la creación del preference link
    const payRequestDto: PayRequestDto = {
      amount: formValue.amount,
      donorEmail: this.authService.getCurrentUserEmail(),
      merenderoId: this.merenderoId
    };

    this.donationService.getPaymentPreferenceLink(payRequestDto)
      .subscribe({
        next: (mpLink) => {
          setTimeout(() => {
            // Trasladamos al usuario al "Preference Link" para que pueda donar
            window.location.href = mpLink.link;
          }, 500);
        },
        error: (err) => {
          this.alertService.error('Error al obtener enlace de pago');
          this.isLoading = false;
        }
      });
  }

  get isFormInvalid(): boolean {
    return this.formReactivo.invalid;
  }
}