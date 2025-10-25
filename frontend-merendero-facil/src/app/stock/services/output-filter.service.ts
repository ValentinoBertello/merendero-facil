import { inject, Injectable } from '@angular/core';
import { DatesService } from '../../shared/services/dates.service';
import { OutputResponseDto } from '../models/movement/output-response.model';

@Injectable({
  providedIn: 'root'
})
export class OutputFilterService {

  private readonly datesService = inject(DatesService);

  /**
     * Método principal que filtra y ordena salidas de insumos
     * **/
  filterAndSortSupplyOutputs(
    outputs: OutputResponseDto[],
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

    let filtered = outputs.filter(output => {
      //filtro por search
      const supplyMatch = searchTerm ? output.supplyName.toLowerCase()
        .includes(searchTerm.toLowerCase()) : true;
      //filtro por fechas
      const dateMatch = this.datesService.filterByDate(output.outputDate, startDate, endDate);
      return dateMatch && supplyMatch;
    });
    // Ordenamiento
    filtered = filtered.sort((a, b) => this.sortOutputs(a, b, sortField, sortDirection));
    return filtered;
  }

  /**
   * Lógica de ordenamiento
   * @param a Salida A
   * @param b Salida B
   * @returns número negativo si a < b, positivo si a > b, 0 si iguales
   * **/
  private sortOutputs(a: any, b: any, sortField: string, sortDirection: string): number {

    if (sortField === 'fecha') {
      const dateA = new Date(a.outputDate).getTime();
      const dateB = new Date(b.outputDate).getTime();
      return sortDirection === 'asc' ? dateA - dateB : dateB - dateA;
    }

    else if (sortField === 'cantidad') {
      const quantityA = a.quantity;
      const quantityB = b.quantity;
      return sortDirection === 'asc' ? quantityA - quantityB : quantityB - quantityA;
    }

    else if (sortField === 'nombre') {
      return sortDirection === 'asc'
        ? a.supplyName.localeCompare(b.supplyName)
        : b.supplyName.localeCompare(a.supplyName);
    }

    return 0;
  }
}
