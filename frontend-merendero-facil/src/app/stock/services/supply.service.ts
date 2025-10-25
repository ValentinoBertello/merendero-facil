import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GlobalService } from '../../shared/services/global-urls.service';
import { SupplyRequestDto } from '../models/supply/supply-request.model';
import { SupplyResponseDto } from '../models/supply/supply-response.model';
import { SupplyCategoryDto } from '../models/supply/supply-category.model';

@Injectable({
  providedIn: 'root'
})
export class SupplyService {

  private readonly http = inject(HttpClient);
  private readonly globalUrls = inject(GlobalService);
  private apiUrl = this.globalUrls.apiUrlLocalHost8082;

  /**
  * Guarda un nuevo insumo asociado a un merendero específico
  **/
  saveSupply(request: SupplyRequestDto, merenderoId: number): Observable<SupplyResponseDto> {
    return this.http.post<SupplyResponseDto>(this.apiUrl + "/supplies/" + merenderoId, request);
  }

  /**
   * Obtiene todos los insumos registrados para un merendero específico
   */
  getSuppliesFromMerendero(merenderoId: number): Observable<SupplyResponseDto[]> {
    return this.http.get<SupplyResponseDto[]>(this.apiUrl + "/supplies/" + merenderoId);
  }

  /**
   * Elimina un insumo específico del inventario de un merendero
   */
  removeSupplyFromMerendero(merenderoId: number, supplyId: number): Observable<number> {
    return this.http.delete<number>(this.apiUrl + "/supplies/" + merenderoId + "/" + supplyId);
  }

  /**
   * Obtiene todas las categorías de insumos disponibles en el sistema
   */
  getSupplyCategories(): Observable<SupplyCategoryDto[]> {
    return this.http.get<SupplyCategoryDto[]>(this.apiUrl + "/supplies/categories")
  }
}
