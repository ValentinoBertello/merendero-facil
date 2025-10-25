import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ItemStockDto } from '../models/inventory/item-stock.model';
import { LotDto } from '../models/inventory/lot.model';
import { GlobalService } from '../../shared/services/global-urls.service';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private readonly http = inject(HttpClient);
  private readonly globalUrls = inject(GlobalService);
  private apiUrl = this.globalUrls.apiUrlLocalHost8082;

  /**
  * Obtiene el inventario completo de stock para un merendero específico
  **/
  getStockInventoryFromMerendero(merenderoId: number): Observable<ItemStockDto[]> {
    return this.http.get<ItemStockDto[]>(this.apiUrl + "/inventory/" + merenderoId)
  }

  /**
  * Obtiene los lotes disponibles para un insumo específico en un merendero
  **/
  getLotsBySupply(merenderoId: number, supplyId: number): Observable<LotDto[]> {
    return this.http.get<LotDto[]>
      (this.apiUrl + "/inventory/lots/" + merenderoId + "/" + supplyId)
  }

  /**
  * Obtiene el stock total disponible para un insumo específico en un merendero
  **/
  getTotalStockBySupply(merenderoId: number, supplyId: number): Observable<number> {
    const url = `${this.apiUrl}/inventory/supply/stock/${merenderoId}/${supplyId}`;
    return this.http.get<number>(url);
  }

  /**
  * Verifica si hay stock suficiente de un insumo para una cantidad específica
  **/
  checkSupplyStock(merenderoId: number, supplyId: number, quantity: number): Observable<number> {
    const url = `${this.apiUrl}/inventory/check-stock/${supplyId}/${quantity}/${merenderoId}`
    return this.http.get<number>(url);
  }

  /**
   * Filtrar y ordenar items stock recibidos por parámetro
  */
  filterAndSortItemsStock(
    items: ItemStockDto[],
    filters: {
      searchTerm?: string;
      selectedFilter?: string;
      sortField: string;
      sortDirection: 'asc' | 'desc';
    }
  ) {
    const {
      searchTerm = '',
      selectedFilter = '',
      sortField = '',
      sortDirection = 'asc'
    } = filters;

    // Aplicamos filtros
    let filtered = items.filter(item => {
      const nameMatch = !searchTerm || item.supplyName.toLowerCase().includes(searchTerm);

      let specialMatch = true;
      if (selectedFilter === 'stock') {
        specialMatch = item.totalStock < item.minQuantity;
      }
      else if (selectedFilter === 'expiration') {
        specialMatch = this.isExpiringSoon(item.nextExpiration);
      }

      return nameMatch && specialMatch;
    });

    // ordenar
    if (sortField) {
      filtered = this.sortData(filtered, sortField, sortDirection);
    }

    return filtered;
  }

  // Función auxiliar para determinar si un producto está próximo a vencer
  isExpiringSoon(expirationDate: string | null): boolean {
    if (!expirationDate) return false;
    const today = new Date();
    const expDate = new Date(expirationDate);
    const diffTime = expDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    return diffDays <= 15 && diffDays >= 0;
  }

  // Ordenamiento
  sortData(items: ItemStockDto[], sortField: string, sortDirection: string) {
    return [...items].sort((a, b) => {
      let comparision = 0;

      switch (sortField) {
        case "name":
          comparision = a.supplyName.localeCompare(b.supplyName);
          break;
        case "stock":
          comparision = a.totalStock - b.totalStock;
          break;
        case "expiration":
          // Manejo especial para fechas
          const dateA = a.nextExpiration ? new Date(a.nextExpiration) : new Date(0);
          const dateB = b.nextExpiration ? new Date(b.nextExpiration) : new Date(0);
          comparision = dateA.getTime() - dateB.getTime();
          break;
      }
      return sortDirection === 'asc' ? comparision : -comparision;
    })
  }
}