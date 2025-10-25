import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { AlertService } from '../../../../shared/services/alert.service';
import { UserService } from '../../../services/user.service';
import { UserRequestDto } from '../../../models/user-request.model';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'app-user-data-step',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-data-step.component.html',
  styleUrl: './user-data-step.component.css'
})
export class UserDataStepComponent {
  private readonly alertService = inject(AlertService);
  private readonly userService = inject(UserService);

  @Input() email!: string;
  @Output() userCreated = new EventEmitter<void>();

  showPassword = false;
  showPasswordRepeat = false;
  formReactivo: FormGroup;

  constructor(private fb: FormBuilder) {
    this.formReactivo = new FormGroup({
      name: new FormControl('', [
        Validators.required, Validators.maxLength(50), Validators.pattern(/^\s*[A-Za-zÀ-ÿ]+(?: [A-Za-zÀ-ÿ]+)*\s*$/)
      ]
      ),
      lastname: new FormControl('', [
        Validators.required, Validators.maxLength(50), Validators.pattern(/^\s*[A-Za-zÀ-ÿ]+(?: [A-Za-zÀ-ÿ]+)*\s*$/)
      ]
      ),
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
  * - Construye el objeto UserPost desde formReactivo y emailToValidate
  * - Llama a userService.postUser para insertar el user.
  */
  onSubmitForm() {
    const formValue = this.formReactivo.value;

    const userPost: UserRequestDto = {
      name: formValue.name,
      lastname: formValue.lastname,
      email: this.email,
      password: formValue.password,
      roleNames: ['ROLE_DONADOR']
    }

    // Enviamos el userPost al back para lo registre
    this.userService.postUser(userPost).subscribe({
      next: () => {
        this.alertService.success('Te has registrado con éxito').then((result) => {
          if (result.isConfirmed) {
            this.userCreated.emit();
          }
        });
      },
      error: (error: any) => {
        console.log(error)
        this.alertService.error("Error", "No se ha podido realizar el registro, intenta más tarde por favor");
      }
    })
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

  // Getter usado para deshabilitar el botón de submit cuando el form es inválido
  get isFormInvalid(): boolean {
    return this.formReactivo.invalid;
  }
}