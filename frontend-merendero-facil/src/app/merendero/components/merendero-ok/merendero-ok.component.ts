import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../user/services/auth.service';
import { AlertService } from '../../../shared/services/alert.service';

@Component({
  selector: 'app-merendero-ok',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './merendero-ok.component.html',
  styleUrl: './merendero-ok.component.css'
})
export class MerenderoOkComponent {

  private readonly alertService = inject(AlertService);
  private readonly authService = inject(AuthService);

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.authService.refreshUserProfile().subscribe({
      next: () => {
        // Roles actualizados, el navbar se renderiza correctamente
        
      },
      error: (err) => {
        console.error('Error refrescando perfil:', err);
        this.alertService.error('Error', 'No se pudo actualizar el perfil de usuario');
      }
    });
  }

  irALista() {
    this.router.navigate(['/list-merenderos']);
  }

}
