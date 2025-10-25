import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserResponseDto } from '../models/user-response.model';
import { UserRequestDto } from '../models/user-request.model';
import { EmailRequestDto } from '../models/email-verification/email-request.model';
import { CodeValidationRequest } from '../models/email-verification/code-validation-request.model';
import { ResetPasswordDto } from '../models/reset-password.model';
import { GlobalService } from '../../shared/services/global-urls.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly globalUrls = inject(GlobalService);
  private readonly urlBase = this.globalUrls.apiUrlLocalHost8080 + "/";
  private readonly http = inject(HttpClient);

  /**
   * Registra un nuevo usuario en el sistema
   */
  postUser(user: UserRequestDto): Observable<UserResponseDto> {
    return this.http.post<UserResponseDto>(this.urlBase + "users/register", user);
  }

  /**
   * Obtiene todos los usuarios registrados en el sistema
   */
  getAllUsers(): Observable<UserResponseDto[]> {
    return this.http.get<UserResponseDto[]>(this.urlBase + "users");
  }

  /**
   * Busca un usuario específico por su dirección de email
   */
  getUserByEmail(email: string): Observable<UserResponseDto> {
    return this.http.get<UserResponseDto>(this.urlBase + "users/email/" + email);
  }

  /**
   * Elimina un usuario del sistema por su identificador único
   */
  deleteUserById(userId: number): Observable<UserResponseDto> {
    return this.http.delete<UserResponseDto>(
      `${this.urlBase}users/delete/${userId}`
    );
  }

  /**
   * Envía un código de verificación al email del usuario
   */
  sendCode(email: EmailRequestDto): Observable<void> {
    return this.http.post<void>(this.urlBase + "verification/send-code", email);
  }

  /**
   * Valida un código de verificación enviado al usuario
   */
  validateCode(request: CodeValidationRequest): Observable<boolean> {
    return this.http.post<boolean>(this.urlBase + 'verification/validate-code', request);
  }

  /**
   * Restablece la contraseña del usuario con el email proporcionado
   */
  resetPassword(email: string, request: ResetPasswordDto): Observable<void> {
    return this.http.put<void>(this.urlBase + "users/reset-password/" + email, request);
  }

  /**
   * Verifica si un email ya está registrado en el sistema
   */
  checkEmailRepeated(email: string): Observable<boolean> {
    return this.http.get<boolean>(this.urlBase + "users/check-email/" + email)
  }

  /**
   * Verifica si un DNI ya está registrado en el sistema
   */
  checkDniRepeated(dni: string): Observable<boolean> {
    return this.http.get<boolean>(this.urlBase + "users/check-dni/" + dni)
  }
}