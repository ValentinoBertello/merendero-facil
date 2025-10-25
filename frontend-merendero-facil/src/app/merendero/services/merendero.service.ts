import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, retry, throwError } from 'rxjs';
import { MpLink } from '../../donation/models/mp-link.model';
import { MerenderoResponseDto } from '../models/merendero-response.model';
import { GlobalService } from '../../shared/services/global-urls.service';

/**
 * Esta clase se encarga exclusivamente de comunicación HTTP con el backend de los Merenderos
 **/
@Injectable({
  providedIn: 'root'
})
export class MerenderoService {

  private readonly globalUrls = inject(GlobalService);
  private readonly urlBase = this.globalUrls.apiUrlLocalHost8081 + "/";
  private readonly http = inject(HttpClient);

  /**
    * Obtenemos un enlace que redirige al dueño del merendero a Mercado Pago
    * para autorizar a nuestra aplicación a actuar en su nombre y que pueda recibir donaciones.
    * Una vez autorizados, insertamos el merendero en la bd con su app token permanente.
    **/
  getAuthorizationUrl(state: string): Observable<MpLink> {
    const encodedState = encodeURIComponent(state);
    return this.http.get<MpLink>(
      `${this.urlBase}mercado-pago/authorize?state=${encodedState}`
    );
  }

  /**
  * Obtiene todos los merenderos registrados en el sistema
  **/
  getAllMerenderos(): Observable<MerenderoResponseDto[]> {
    return this.http.get<MerenderoResponseDto[]>(this.urlBase + "merenderos");
  }

  /**
  * Busca un merendero específico por el email de su administrador
  **/
  getMerenderoByManagerEmail(managerEmail: String): Observable<MerenderoResponseDto> {
    return this.http.get<MerenderoResponseDto>(this.urlBase + "merenderos/byManager/" + managerEmail);
  }

  /**
  * Obtiene los merenderos más cercanos a una ubicación geográfica específica
  **/
  getClosestMerenderos(size: number, latitude: number, longitude: number): Observable<MerenderoResponseDto[]> {
    console.log(this.urlBase + "merenderos/close/" + size + "/" + latitude + "/" + longitude)
    return this.http.get<MerenderoResponseDto[]>(this.urlBase + "merenderos/close/" + size + "/" + latitude + "/" + longitude).pipe(
      retry(2), // <-- Reintentar 2 veces ante errores
      catchError(error => {
        console.error('Error fetching merenderos:', error);
        return throwError(() => error);
      })
    );;
  }

  /**
  * Elimina un merendero del sistema por su identificador único
  **/
  deleteMerenderoById(merenderoId: number): Observable<MerenderoResponseDto> {
    return this.http.delete<MerenderoResponseDto>(
      `${this.urlBase}merenderos/delete/${merenderoId}`
    );
  }
}