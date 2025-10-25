import { CommonModule } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { AuthService } from '../../../../user/services/auth.service';
import { MovementsService } from '../../../services/movements.service';
import { EntryResponseDto } from '../../../models/movement/entry-response.model';
import { AlertService } from '../../../../shared/services/alert.service';
import { ExportsService } from '../../../../shared/services/exports.service';
import { DatesService } from '../../../../shared/services/dates.service';
import { catchError, of, switchMap, tap } from 'rxjs';
import { EntryFilterService } from '../../../services/entry-filter.service';
import { CurrencyArsPipe } from '../../../../shared/pipes/currency-ars.pipe';
import { WindowService } from '../../../../shared/services/window.service';


@Component({
  selector: 'app-entradas',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule, CurrencyArsPipe],
  templateUrl: './entradas.component.html',
  styleUrl: './entradas.component.css'
})
export class EntradasComponent {
  // Inyección de dependencias
  private readonly entryFilter = inject(EntryFilterService);
  private readonly datesService = inject(DatesService);
  private readonly exportService = inject(ExportsService);
  private readonly alertService = inject(AlertService);
  private readonly authService = inject(AuthService);
  private readonly movementsService = inject(MovementsService);
  private readonly windowService = inject(WindowService);

  // Propiedades
  entries: EntryResponseDto[] = [];
  filteredEntries: EntryResponseDto[] = [];
  isLoading: boolean = false;
  shouldAnimateApplyButton = true;

  currentPage: number = 1;
  itemsPerPage: number = 5;

  //Filtros
  startDate?: string;
  endDate?: string;
  searchSupplies = '';
  sortField: string = 'cantidad'
  sortDirection: 'asc' | 'desc' = 'desc';

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.loadEntries();
    this.setDefaultDates();
    this.applyFilters();
  }

  // Al cambio de tamaño recalcula cuántos ítems entran por página y vuelve a aplicar los filtros
  @HostListener('window:resize')
  onResize(): void {
    this.itemsPerPage = this.windowService.getPageSize();
  }

  /**
  * Aplica los filtros actuales a la lista de entradas de insumos
  **/
  applyFilters() {
    this.shouldAnimateApplyButton = false;
    const options = {
      searchTerm: this.searchSupplies,
      startDate: this.startDate,
      endDate: this.endDate,
      sortField: this.sortField,
      sortDirection: this.sortDirection
    };

    this.filteredEntries = this.entryFilter.filterAndSortSupplyEntries(this.entries, options);
  }

  /**
   * Reactiva animación de botón "aplicar" cuando cambia algún filtro 
   **/
  onAnyFilterChange(): void {
    if (!this.shouldAnimateApplyButton) {
      this.shouldAnimateApplyButton = true;
    }
  }

  /**
  * Navega a la página de agregar nueva entrada de insumos
  **/
  navigateToAddEntry() {
    this.router.navigate(['/stock/add-entry']);
  }

  /**
   * Alterna la dirección de ordenamiento entre ascendente y descendente
   **/
  toggleSortDirection() {
    this.sortDirection = this.sortDirection === 'asc' ?
      'desc' : 'asc';
  }

  exportExcel() {
    // Transformar cada entrada a un objeto plano que Excel entienda
    const entriesToExport = this.filteredEntries.map(entry => ({
      'Fecha': new Date(entry.entryDate).toLocaleDateString('es-AR'),
      'Insumo': entry.supplyName,
      'Cantidad': entry.quantity,
      'Costo': (entry.entryType === 'PURCHASE' && entry.cost != null)
        ? `$${entry.cost.toFixed(2)}`
        : 'Donación'
    }));

    this.exportService.exportExcel(entriesToExport, 'entradas', 'Entradas');
  }

  async exportPdf() {
    // Preparar datos para la tabla (cada fila es un arreglo)
    const entriesData = this.filteredEntries.map(entry => {
      let unidad = '';
      if (entry.unit === 'KG') unidad = 'kg';
      else if (entry.unit === 'LITRO') unidad = 'L';
      else unidad = 'unidades';
      const costoTexto =
        entry.entryType === 'PURCHASE' && entry.cost != null
          ? `$${entry.cost.toFixed(2)}`
          : 'Donación';
      return [
        new Date(entry.entryDate).toLocaleDateString('es-AR'),
        entry.supplyName,
        `${entry.quantity} ${unidad}`,
        costoTexto
      ];
    });

    const headers = ['Fecha', 'Insumo', 'Cantidad', 'Costo'];
    this.exportService.exportPdf(entriesData, headers, 'entradas', 'Reporte de Entrada de insumos', this.startDate, this.endDate);
  }

  /**
  * Carga las entradas de insumos del merendero autenticado desde el backend
  **/
  private loadEntries() {
    this.authService.getMerenderoIdOfUser().pipe(
      // Cuando llegue el merenderoId, enacadenamos la llamada de entradas de insumos
      switchMap(merenderoId => {
        return this.movementsService.getEntriesFromMerendero(merenderoId);
      }),
      // cuando lleguen las donaciones, seteamos donaciones y aplicamos filtros
      tap(entries => {
        this.entries = entries;
        this.filteredEntries = [...entries];
        this.applyFilters();
      }),
      catchError(err => {
        console.error('Error:', err);
        this.alertService.error('Ocurrió un error inesperado');
        return of([]);
      })
    ).subscribe();
  }

  /**
  * Se setean las fechas de filtrado por defecto usando el datesService 
  * (del 1 de mayo de este año al 1 de junio de este año)
  **/
  private setDefaultDates() {
    const currentYear = new Date().getFullYear();
    const range = this.datesService.getDefaultDateRange(
      new Date(currentYear, 4, 1),
      new Date(currentYear, 5, 1)
    );

    this.startDate = range.startDate;
    this.endDate = range.endDate;
  }
}