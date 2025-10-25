import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { catchError, map, Observable, of } from 'rxjs';
import { UserService } from '../../services/user.service';
import { EmailRequestDto } from '../../models/email-verification/email-request.model';
import { AlertService } from '../../../shared/services/alert.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-email-step',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './email-step.component.html',
  styleUrl: './email-step.component.css'
})
export class EmailStepComponent {
  private readonly alertService = inject(AlertService);
  private readonly userService = inject(UserService);

  @Input() mode: 'registration' | 'forgot-password' = 'registration';
  @Output() email = new EventEmitter<string>();

  emailForm: FormGroup;
  isSendingCode = false;

  constructor(private fb: FormBuilder) {
    this.emailForm = this.fb.group({
      email: ["", [Validators.required, Validators.email], [this.repeatedEmailValidator()]]
    });
  }

  /**
  * Paso 1: Toma el email del formulario y solicita al backend que envíe un código.
  */
  sendCode() {
    this.isSendingCode = true;
    const email = this.emailForm.value.email!;
    const emailRequest: EmailRequestDto = {
      email: email
    }
    this.userService.sendCode(emailRequest).subscribe({
      next: () => {
        this.isSendingCode = false;
        this.email.emit(email);
      },
      error: (error: any) => {
        console.log("error al enviar el código: " + error)
        this.isSendingCode = false;
        this.alertService.error("Error", "Error al enviar el código, intenta más tarde por favor");
      }
    })
  }

  /**
  * Validador asíncrono que consulta al backend si el email ya existe.
  * - Si el campo está vacío devuelve null inmediatamente.
  */
  private repeatedEmailValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }

      const email = control.value;

      // llamamos a userService.checkEmailRepeated(email) y chequeamos
      return this.userService.checkEmailRepeated(email).pipe(
        map(emailExists => {
          if (this.mode === 'registration') {
            // Para registro: email NO debe existir
            return emailExists ? { repeatedEmailError: true } : null;
          } else {
            // Para forgot-password: email DEBE existir  
            return emailExists ? null : { emailNotExists: true };
          }
        }),
        catchError((error) => {
          console.error('Error validating the email', error);
          return of(null);
        })
      );
    };
  }
}