import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CodeValidationRequest } from '../../models/email-verification/code-validation-request.model';
import { UserService } from '../../services/user.service';
import { AlertService } from '../../../shared/services/alert.service';
import { EmailRequestDto } from '../../models/email-verification/email-request.model';

@Component({
  selector: 'app-code-step',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './code-step.component.html',
  styleUrl: './code-step.component.css'
})
export class CodeStepComponent {
  private readonly alertService = inject(AlertService);
  private readonly userService = inject(UserService);

  @Input() email!: string;
  @Output() codeValidated = new EventEmitter<void>();

  countdown = 0;
  isSendingCode = false;
  codeForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.codeForm = this.fb.group({
      code: ['', [Validators.required, Validators.pattern(/^(\s*\d\s*){6}$/)]]
    });
  }

  /**
  * Paso 2: Valida el código del codeForm contra el backend.
  * Si es válido, se avanza al paso 3.
  */
  validateCode() {
    if (this.codeForm.invalid) return;

    // quitamos espacios por si el usuario los puso
    const code = this.codeForm.value.code!.replace(/\s+/g, '');

    const codeValidationRequest: CodeValidationRequest = {
      email: this.email,
      code: code
    }

    // Mandamos email con codigo para que el back valide
    this.userService.validateCode(codeValidationRequest).subscribe({
      next: (isValid: boolean) => {

        if (isValid) {
          // Avanzamos al paso 3
          this.codeValidated.emit();
        } else {
          this.alertService.error("Código incorrecto");
        }
      },
      error: (error: any) => {
        console.log("error al enviar el código: " + error)
        this.alertService.error("Error", "Error al validar el código, intente más tarde por favor");
      }
    });
  }

  /**
  * Reenvía el código de verificación al email proporcionado en el @Input.
  **/
  resendCode() {
    if (this.countdown > 0) return;

    this.isSendingCode = true;

    const emailRequest: EmailRequestDto = {
      email: this.email
    };

    this.userService.sendCode(emailRequest).subscribe({
      next: () => {
        this.isSendingCode = false;
        this.startCountdown();
      },
      error: (error: any) => {
        console.log("error al reenviar el código: " + error)
        this.isSendingCode = false;
        this.alertService.error("Error", "Error al reenviar el código, intenta más tarde por favor");
      }
    });
  }

  /**
   * Inicia el temporizador de 30 segundos para evitar reenvíos consecutivos
   */
  private startCountdown() {
    this.countdown = 30;
    const interval = setInterval(() => {
      this.countdown--;
      if (this.countdown === 0) clearInterval(interval);
    }, 1000);
  }
}