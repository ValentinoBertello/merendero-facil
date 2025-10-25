import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService } from '../../services/user.service';
import { UserResponseDto } from '../../models/user-response.model';

@Component({
  selector: 'app-list-users',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './list-users.component.html',
  styleUrl: './list-users.component.css'
})
export class ListUsersComponent {
  public users: UserResponseDto[] = [];
  private readonly userService = inject(UserService);

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: (usersGet) => {
        this.users = usersGet;
      },
      error: (err) => {
        alert("No se puedo traer a los users activos")
      }
    });
  }

  eliminarUser(id: number) {
    const isConfirmed = window.confirm('¿Estás seguro de que deseas eliminar este USUARIO? Esta acción no se puede deshacer.');
    if (isConfirmed) {
      this.userService.deleteUserById(id).subscribe({
        next: () => {
          // Removerlo de la lista local para actualizar la vista
          this.users = this.users.filter(m => m.id !== id);
        },
        error: err => {
          console.error(err);
        }
      });
    }

  }
}
