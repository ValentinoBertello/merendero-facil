import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GlobalService } from '../../shared/services/global-urls.service';
import { EntryResponseDto } from '../models/movement/entry-response.model';
import { EntryRequestDto } from '../models/movement/entry-request.model';
import { OutputRequestDto } from '../models/movement/output-request.model';
import { OutputResponseDto } from '../models/movement/output-response.model';
import { SummaryMovementsDto } from '../models/dashboard/summary-movements.model';

@Injectable({
  providedIn: 'root'
})
export class MovementsService {

  private readonly http = inject(HttpClient);
  private readonly globalUrls = inject(GlobalService);
  private apiUrl = this.globalUrls.apiUrlLocalHost8082;

  /**
  * Guarda una nueva entrada de insumos en el sistema
  **/
  saveEntry(entryRequest: EntryRequestDto): Observable<EntryResponseDto> {
    return this.http.post<EntryResponseDto>(this.apiUrl + "/entries", entryRequest);
  }

  /**
  * Obtiene todas las entradas de insumos de un merendero específico
  **/
  getEntriesFromMerendero(merenderoId: number): Observable<EntryResponseDto[]> {
    return this.http.get<EntryResponseDto[]>(this.apiUrl + "/entries/" + merenderoId);
  }

  /**
  * Registra una nueva salida/consumo de insumos del inventario
  **/
  saveOutput(outputRequest: OutputRequestDto): Observable<OutputResponseDto> {
    return this.http.post<OutputResponseDto>(this.apiUrl + "/outputs", outputRequest);
  }

  /**
  * Obtiene todas las salidas/consumos de insumos de un merendero específico
  **/
  getOutputsFromMerendero(merenderoId: number): Observable<OutputResponseDto[]> {
    return this.http.get<OutputResponseDto[]>(this.apiUrl + "/outputs/" + merenderoId);
  }

  /**
  * Obtiene un resumen de movimientos filtrado por fechas y agrupamiento, que se usará
  * en el dasbhoard de movimientos
  **/
  getSummaryMovements(
    merenderoId: number,
    supplyId: number,
    startDate: string,   // formato: 'yyyy-MM-dd'
    endDate: string,     // formato: 'yyyy-MM-dd'
    groupBy: 'day' | 'week' | 'month'
  ): Observable<SummaryMovementsDto> {
    const url = `${this.apiUrl}/dashboard/movements/${merenderoId}/${supplyId}/${startDate}/${endDate}/group/${groupBy}`;
    console.log(url);
    return this.http.get<SummaryMovementsDto>(url);
  }
}