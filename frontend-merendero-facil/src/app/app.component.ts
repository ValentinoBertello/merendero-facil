import { Component, HostListener } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterModule, RouterOutlet } from '@angular/router';
import Swal from 'sweetalert2';
import { AuthService } from './user/services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, RouterLink, CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {

  // Esta propiedad controlará si mostramos o no la navbar de stock:
  public showStockNavbar = false;

  constructor(public authService: AuthService, private router: Router) {
    // Nos suscribimos a los cambios de ruta para actualizar showStockNavbar
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.showStockNavbar = event.urlAfterRedirects.startsWith('/stock');
      }
    });
  }

  // Cerramos sesión y trasladamos al login
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login-user']);
  }

  // Si el user esta autenticado le deja crear, sino le sugiere iniciar sesion
  handleCreateMerendero(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/create-merendero']);
    } else {
      this.showLoginAlert();
    }
  }

  // Login alert
  private showLoginAlert(): void {
    Swal.fire({
      title: 'Debes loguearte!',
      text: 'Para registrar un merendero debes iniciar sesión',
      icon: 'info',
      showCancelButton: true,
      confirmButtonColor: '#007bff',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Iniciar sesión',
      cancelButtonText: 'Cancelar',
      background: '#f8f9fa',
      iconColor: '#0d6efd'
    }).then((result) => {
      if (result.isConfirmed) {
        this.router.navigate(['/login-user']);
      }
    });
  }

  // Cerar sesión alert
  public showLogoutAlert(): void {
    Swal.fire({
      title: '¿Estás seguro de cerrar sesión?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#007bff',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Si',
      cancelButtonText: 'Cancelar',
      background: '#f8f9fa',
      iconColor: '#0d6efd'
    }).then((result) => {
      if (result.isConfirmed) {
        this.logout();
      }
    });
  }
}
