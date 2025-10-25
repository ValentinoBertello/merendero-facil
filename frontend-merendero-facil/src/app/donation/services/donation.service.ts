import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { PayRequestDto } from '../models/pay-request.model';
import { MpLink } from '../models/mp-link.model';
import { GlobalService } from '../../shared/services/global-urls.service';
import { DonationDashboardResponse } from '../models/dashboard/donation-dashboard.model';
import { Page } from '../models/page.model';
import { DonationResponseDto } from '../models/donation-response.model';

@Injectable({
  providedIn: 'root'
})
export class DonationService {

  private readonly http = inject(HttpClient);
  private readonly globalUrls = inject(GlobalService);
  private readonly urlBase = this.globalUrls.apiUrlLocalHost8081 + "/";

  /**
  * Obtiene todas las donaciones de un merendero específico sin paginación
  **/
  getAllDonationsByMerenderoId(merenderoId: number): Observable<any> {
    const url = `${this.globalUrls.apiUrlLocalHost8081}/donations/${merenderoId}`;
    return this.http.get<any>(url).pipe(
      tap(),
      catchError(error => {
        console.error('Error completo:', error);
        return throwError(() => error);
      })
    );
  }

  /**
  * Obtiene donaciones paginadas con filtros opcionales por fecha, email y ordenamiento
  **/
  getDonationPagesByFilters(
    filters: { merenderoId?: number, sinceDate?: string; untilDate?: string; email?: string; },
    page: number,
    size: number = 10,
    sort?: string
  ): Observable<Page<DonationResponseDto>> {
    // Parámetros de paginación obligatorios
    let query = `?page=${page}&size=${size}`;
    query += `&merenderoId=${filters.merenderoId}`

    // Si tenemos rango de fechas completo, lo agregamos
    if (filters.sinceDate && filters.untilDate) {
      query += `&sinceDate=${filters.sinceDate}&untilDate=${filters.untilDate}`
    }
    // Filtro adicional por email de donante
    if (filters.email) {
      query += `&donorEmail=${filters.email}`
    }
    // Si nos pasaron un sort lo incluimos también
    if (sort) {
      query += `&sort=${sort}`;
    }

    // Construimos la URL completa y lanzamos la petición GET
    const url = `${this.urlBase}donations/search${query}`;
    console.log("llamando a: " + url);
    return this.http.get<Page<DonationResponseDto>>(url);
  }

  /**
    * Se obtiene un objeto con todos los datos necesarios para el dashboard de donaciones.
    **/
  getDonationDashboard(merenderoId: number, startDate: string, endDate: string,
    groupBy: string
  ): Observable<DonationDashboardResponse> {
    return this.http.get<DonationDashboardResponse>(this.urlBase + "reports/" + merenderoId +
      "/" + startDate + "/" + endDate + "/group/" + groupBy);
  }

  /**
    * Obtenemos un enlace único para que el donante complete el pago (haga una donación)
    **/
  getPaymentPreferenceLink(payRequestDto: PayRequestDto): Observable<MpLink> {
    return this.http.post<MpLink>(this.urlBase + "mercado-pago/preference", payRequestDto);
  }
}