import { inject, Injectable } from '@angular/core';
import { EntryResponseDto } from '../models/movement/entry-response.model';
import { DatesService } from '../../shared/services/dates.service';

@Injectable({
  providedIn: 'root'
})
export class EntryFilterService {

  private readonly datesService = inject(DatesService);

  /**
   * Método principal que filtra y ordena entradas de insumos
   * **/
  filterAndSortSupplyEntries(
    entries: EntryResponseDto[],
    filters: {
      searchTerm?: string;
      startDate?: string;
      endDate?: string;
      sortField: string;
      sortDirection: 'asc' | 'desc';
    }
  ) {
    const {
      searchTerm = '',
      startDate = '',
      endDate = '',
      sortField = '',
      sortDirection = 'asc'
    } = filters;

    let filtered = entries.filter(entry => {
      //filtro por search
      const supplyMatch = searchTerm ? entry.supplyName.toLowerCase()
        .includes(searchTerm.toLowerCase()) : true;
      //filtro por fechas
      const dateMatch = this.datesService.filterByDate(entry.entryDate, startDate, endDate);
      return dateMatch && supplyMatch;
    });
    // Ordenamiento
    filtered = filtered.sort((a, b) => this.sortEntries(a, b, sortField, sortDirection));
    return filtered;
  }

  /**
   * Lógica de ordenamiento
   * @param a Entrada A
   * @param b Entrada B
   * @returns número negativo si a < b, positivo si a > b, 0 si iguales
   * **/
  private sortEntries(a: any, b: any, sortField: string, sortDirection: string): number {
    if (sortField === 'fecha') {
      const dateA = new Date(a.entryDate).getTime();
      const dateB = new Date(b.entryDate).getTime();
      return sortDirection === 'asc' ? dateA - dateB : dateB - dateA;
    }
    else if (sortField === 'cantidad') {
      const quantityA = a.quantity;
      const quantityB = b.quantity;
      return sortDirection === 'asc' ? quantityA - quantityB : quantityB - quantityA;
    }
    else if (sortField === 'costo') {
      const costoA = a.cost ?? 0;
      const costoB = b.cost ?? 0;
      return sortDirection === 'asc' ? costoA - costoB : costoB - costoA;
    }
    else if (sortField === 'nombre') {
      return sortDirection === 'asc'
        ? a.supplyName.localeCompare(b.supplyName)
        : b.supplyName.localeCompare(a.supplyName);
    }
    return 0;
  }
}