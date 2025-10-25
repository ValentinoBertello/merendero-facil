import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { ResetPasswordDto } from '../../../models/reset-password.model';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-reseat-password-step',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reseat-password-step.component.html',
  styleUrl: './reseat-password-step.component.css'
})
export class ReseatPasswordStepComponent {
  private readonly userService = inject(UserService);

  @Input() email!: string;
  @Output() passwordChanged = new EventEmitter<void>();

  showPassword = false;
  showPasswordRepeat = false;
  formReactivo: FormGroup;

  constructor(private fb: FormBuilder) {
    this.formReactivo = new FormGroup({
      password: new FormControl('', [
        Validators.required,
        Validators.maxLength(35),
        Validators.minLength(8)
      ], [this.createPasswordValidator()]
      ),
      repeatPassword: new FormControl('', [
        Validators.required,])
    }, { validators: this.passwordMatchValidator }); // validador a nivel de formgroup
  }

  /**
  * - Construye el objeto ResetPasswordDto
  * - Llama a userService.postUser para cambiar la contraseña.
  */
  onSubmitForm() {
    const formValue = this.formReactivo.value;

    const resetPasswordDto: ResetPasswordDto = {
      password: formValue.password
    }

    this.userService.resetPassword(this.email, resetPasswordDto).subscribe({
      next: () => {
        Swal.fire({
          title: 'Contraseña actualizada con éxito!',
          icon: 'success',
          confirmButtonText: 'Aceptar',
          allowOutsideClick: false // Evita que cierre haciendo click fuera
        }).then((result) => {
          if (result.isConfirmed) {
            this.passwordChanged.emit();
          }
        });
      },
      error: (error: any) => {
        console.log(error)
        Swal.fire({
          title: 'Error!',
          text: 'No se pudo actualizar la contraseña',
          icon: 'error',
          confirmButtonText: 'Cerrar',
          confirmButtonColor: '#A0522D'
        });
      }
    })
  }

  /**
  * Validador a nivel de FormGroup que chequea si password === repeatPassword.
  */
  private passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const form = control as FormGroup;
    const password = form.get('password')?.value;
    const repeatPassword = form.get('repeatPassword')?.value;

    if (password === repeatPassword) {
      return null; // Las contraseñas coinciden: NO hay error
    } else {
      return { passwordMismatch: true }; // Contraseñas NO coinciden: hay error
    }
  }

  /**
  * Validador asíncrono que verifica requisitos en la contraseña
  */
  private createPasswordValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }
      const password = control.value;

      // Al menos 1 mayúscula
      if (!/[A-Z]/.test(password)) {
        return of({
          AtLeastOneMayus: true
        });
      }

      // Al menos una letra minúscula
      if (!/[a-z]/.test(password)) {
        return of({
          AtLeastOneMinus: true
        });
      }

      // Al menos 1 número
      if (!/[0-9]/.test(password)) {
        return of({
          AtLeastOneNumber: true
        });
      }

      // 5. Al menos 1 carácter especial
      if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
        return of({
          AtLeastOneEspecialCarac: true
        });
      }

      // 6. Validar espacios en blanco
      if (/\s/.test(password)) {
        return of({
          WhiteSpaceError: true
        });
      }

      // Si todo está bien, retorna un Observable null
      return of(null);
    }
  }

  // Getter usado para deshabilitar el botón de submit cuando el form es inválido
  get isFormInvalid(): boolean {
    return this.formReactivo.invalid;
  }
}