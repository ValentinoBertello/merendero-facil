import { Injectable } from '@angular/core';
import { MerenderoResponseDto } from '../models/merendero-response.model';

export interface Filters {
  openNow: boolean;
  capacityMin: number | null;
  openEveryDay: boolean;
}
/**
 * Esta clase se encarga exclusivamente de lógica de filtrado de MerenderoResponseDto[]
 * **/
@Injectable({
  providedIn: 'root'
})
export class MerenderosFilterService {

  private readonly allDays = [
    'LUNES', 'MARTES', 'MIÉRCOLES', 'JUEVES',
    'VIERNES', 'SÁBADO', 'DOMINGO'
  ];

  filterMerenderos(
    merenderos: MerenderoResponseDto[],
    filters: Filters,
    filterText: string = ''): MerenderoResponseDto[] {
    let filtered = [...merenderos];

    // 1) Filtro de texto (nombre o dirección)
    if (filterText) {
      const searchText = filterText.toLowerCase();
      filtered = filtered.filter(m =>
        m.name.toLowerCase().includes(searchText) ||
        m.address.toLowerCase().includes(searchText)
      );
    }

    // 2) Filtro "Abiertos ahora"
    if (filters.openNow) {
      filtered = filtered.filter(m => m.openNow);
    }

    // 3) Filtro de capacidad mínima
    if (filters.capacityMin !== null && filters.capacityMin !== undefined) {
      filtered = filtered.filter(m => m.capacity >= filters.capacityMin!);
    }

    // 4) Filtro "Abre todos los días"
    if (filters.openEveryDay) {
      filtered = filtered.filter(m => this.opensEveryDay(m));
    }

    return filtered;
  }

  private opensEveryDay(merendero: MerenderoResponseDto): boolean {
    const diasString = merendero.daysOpen.toUpperCase();
    const diasArray = diasString
      .split(/[,|\-]/)
      .map(d => d.trim());

    return this.allDays.every(dia => diasArray.includes(dia));
  }
}