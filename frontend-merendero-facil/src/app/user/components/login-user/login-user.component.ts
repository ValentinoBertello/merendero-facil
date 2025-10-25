import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequestDto } from '../../models/login-request.model';
import { AlertService } from '../../../shared/services/alert.service';

@Component({
  selector: 'app-login-user',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login-user.component.html',
  styleUrl: './login-user.component.css'
})
export class LoginUserComponent {
  private readonly alertService = inject(AlertService);
  private readonly authService = inject(AuthService);

  isLoading = false;
  mostrarPassword: boolean = false;
  formReactivo: FormGroup;

  constructor(private router: Router) {
    this.formReactivo = new FormGroup({
      email: new FormControl('', [
        Validators.required
      ]
      ),
      password: new FormControl('', [
        Validators.required,
      ]
      )
    });
  }

  onSubmitForm() {
    this.isLoading = true;
    const formValue = this.formReactivo.value;

    // Crear objeto de LoginRequest
    const loginRequest: LoginRequestDto = {
      email: formValue.email,
      password: formValue.password
    };

    // Enviar Login
    this.authService.login(loginRequest).subscribe({
      next: () => {
        this.isLoading = false;
        this.alertService.success('Te logueaste con éxito!').then(() => {
          this.router.navigate(['/list-merenderos']);
        });
      },
      error: (error: any) => {
        this.isLoading = false;
        this.alertService.error("No se pudo iniciar sesión", "Revisá los datos ingresados");
      }
    });
  }
}