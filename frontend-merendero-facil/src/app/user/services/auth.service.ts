import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, map, Observable, of, tap } from 'rxjs';
import { MerenderoService } from '../../merendero/services/merendero.service';
import { LoginRequestDto } from '../models/login-request.model';
import { LoginResponseDto } from '../models/login-response.model';
import { GlobalService } from '../../shared/services/global-urls.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly merenderoService = inject(MerenderoService);
  private readonly http = inject(HttpClient);
  private readonly globalUrls = inject(GlobalService);
  private readonly router = inject(Router);
  private apiUrl = this.globalUrls.apiUrlLocalHost8080;

  // Las siguientes 2 variables son para seguir el estado de si alguien está logueado o no en todo momento

  // Variable reactiva que empieza en false, o sea, diciendo que el usuario no está autenticado
  // Se puede escuchar o suscribirse a su valor y se actualiza automáticamente.
  private authStatus = new BehaviorSubject<boolean>(false);

  //Convierte ese BehaviorSubject en un Observable solo de lectura,
  // para que otros componentes puedan escuchar si el estado cambia, pero no puedan modificarlo directamente.
  authStatus$ = this.authStatus.asObservable();

  // estado reactivo del perfil/roles
  private userProfileSubject = new BehaviorSubject<any | null>(null);
  userProfile$ = this.userProfileSubject.asObservable();

  private parseJwt(token: string | null) {
    if (!token) return null;
    const parts = token.split('.');
    if (parts.length !== 3) return null; // JWT debe tener 3 partes
    try {
      // JWT payload está en Base64Url, reemplazamos para atob
      const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const json = atob(payload);
      return JSON.parse(json);
    } catch {
      return null;
    }
  }

  login(loginDto: LoginRequestDto): Observable<LoginResponseDto> {
    return this.http.post<LoginResponseDto>(`${this.apiUrl}/login`, loginDto).pipe(
      tap(response => {

        //El localStorage es una forma de guardar datos en el navegador del usuario,
        //  como si fuera una pequeña "base de datos" que se guarda en su computadora.

        // Guardar el token en localStorage
        localStorage.setItem('token', response.token);

        // Actualiza el estado a "autenticado"
        this.authStatus.next(true);
      }),
      catchError(error => {

        if (error.status === 401) {
          throw new Error('Credenciales incorrectas');
        }
        throw new Error('Error en el servidor');
      })
    );
  }

  // Retorna el token almacenado en el localStorage
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Cerramos sesión, o sea, se borra el token, el estado de authStatus es falso (no autenticado)
  // y volvemos al login
  logout(): void {
    localStorage.removeItem('token');
    this.authStatus.next(false);
    this.router.navigate(['/login']);
  }

  // Nos dice si el usuario esta o no autenticado 
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // Método para obtener email del usuario (necesitas decodificar el JWT)
  getCurrentUserEmail(): string {
    const payload = this.parseJwt(this.getToken());
    if (!payload) return '';
    return payload.username || '';
  }

  getCurrentUserRoles(): string[] {
    // Primero intentamos desde el perfil cargado
    const profile = this.userProfileSubject.getValue();
    if (profile?.roles) return profile.roles;

    // Si no hay perfil, intentamos decodificar el JWT
    const payload = this.parseJwt(this.getToken());
    return payload?.authorities || [];
  }

  hasRole(role: string): boolean {
    const roles = this.getCurrentUserRoles();
    // Comprobamos en todas las variantes posibles
    return roles.includes(role) || roles.includes(`ROLE_${role}`) || roles.includes(role.toUpperCase());
  }

  private merenderoId = 0;
  // Método para obtener el merendero id del usuario si es que tiene
  getMerenderoIdOfUser(): Observable<number> {
    if (this.merenderoId !== 0) {
      return of(this.merenderoId);
    }
    if (!this.hasRole('ROLE_ENCARGADO')) {
      return of(0);
    }
    return this.merenderoService.getMerenderoByManagerEmail(this.getCurrentUserEmail()).pipe(
      tap(merendero => { this.merenderoId = merendero.id; }),
      map(merendero => merendero.id),
      catchError(err => {
        console.error('Error al obtener merendero en AuthService:', err);
        return of(0);
      })
    );
  }

  // Método público para forzar recarga del perfil
  refreshUserProfile(): Observable<any> {
    // endpoint /auth/me que devuelve { username, authorities, ... }
    return this.http.get<any>(`${this.apiUrl}/users/auth/me`).pipe(
      tap(profile => {
        this.userProfileSubject.next(profile);
      }),
      catchError(err => {
        console.error('Error refrescando perfil', err);
        return of(null);
      })
    );
  }
}