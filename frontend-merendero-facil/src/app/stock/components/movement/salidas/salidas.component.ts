import { CommonModule } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { AuthService } from '../../../../user/services/auth.service';
import { ExportsService } from '../../../../shared/services/exports.service';
import { MovementsService } from '../../../services/movements.service';
import { OutputResponseDto } from '../../../models/movement/output-response.model';
import { AlertService } from '../../../../shared/services/alert.service';
import { catchError, of, switchMap, tap } from 'rxjs';
import { DatesService } from '../../../../shared/services/dates.service';
import { OutputFilterService } from '../../../services/output-filter.service';
import { WindowService } from '../../../../shared/services/window.service';

@Component({
  selector: 'app-salidas',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule],
  templateUrl: './salidas.component.html',
  styleUrl: './salidas.component.css'
})
export class SalidasComponent {
  // Inyección de dependencias
  private readonly outputFilter = inject(OutputFilterService);
  private readonly datesService = inject(DatesService);
  private readonly alertService = inject(AlertService);
  private readonly authService = inject(AuthService);
  private readonly movementsService = inject(MovementsService);
  private readonly exportService = inject(ExportsService);
  private readonly windowService = inject(WindowService);

  // Propiedades
  outputs: OutputResponseDto[] = [];
  filteredOutputs: OutputResponseDto[] = [];
  shouldAnimateApplyButton = true;

  currentPage: number = 1;
  itemsPerPage: number = 6;
  isLoading: boolean = false;

  //Filtros
  startDate?: string;
  endDate?: string;
  searchSupplies = '';
  sortField: string = 'fecha'
  sortDirection: 'asc' | 'desc' = 'desc';

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.loadOutputs();
    this.setDefaultDates();
    this.applyFilters();
    this.itemsPerPage = this.windowService.getPageSize();
  }

  // Al cambio de tamaño recalcula cuántos ítems entran por página y vuelve a aplicar los filtros
  @HostListener('window:resize')
  onResize(): void {
    this.itemsPerPage = this.windowService.getPageSize();
  }

  /**
  * Aplica los filtros actuales a la lista de salidas de insumos
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

    this.filteredOutputs = this.outputFilter.filterAndSortSupplyOutputs(this.outputs, options);
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
  * Navega a la página de agregar nueva salida de insumos
  **/
  navigateToAddOutput() {
    this.router.navigate(['/stock/add-output']);
  }

  /**
   * Alterna la dirección de ordenamiento entre ascendente y descendente
   **/
  toggleSortDirection() {
    this.sortDirection = this.sortDirection === 'asc' ?
      'desc' : 'asc';
  }

  // Exports
  exportExcel() {
    // Transformar cada salida a un objeto plano que Excel entienda
    const outputsToExport = this.filteredOutputs.map(output => ({
      'Fecha': new Date(output.outputDate).toLocaleDateString('es-AR'),
      'Insumo': output.supplyName,
      'Cantidad': output.quantity,
      'Unidad': (() => {
        switch (output.unit) {
          case 'KG': return 'kg';
          case 'LITRO': return 'L';
          default: return 'unidades';
        }
      })()
    }));
    this.exportService.exportExcel(outputsToExport, 'salidas', 'Salidas');
  }

  async exportPdf() {
    const outputsData = this.filteredOutputs.map(o => [
      new Date(o.outputDate).toLocaleDateString('es-AR'),
      o.supplyName,
      o.quantity.toString(),
      o.unit === 'KG' ? 'kg' : o.unit === 'LITRO' ? 'L' : 'unidades'
    ]);

    const headers = ['Fecha', 'Insumo', 'Cantidad', 'Unidad'];

    this.exportService.exportPdf(outputsData, headers, 'salidas', 'Reporte de Salidas', this.startDate, this.endDate);
  }

  /**
  * Carga las salidas de insumos del merendero autenticado desde el backend
  **/
  private loadOutputs() {
    this.authService.getMerenderoIdOfUser().pipe(
      // Cuando llegue el merenderoId, enacadenamos la llamada de entradas de insumos
      switchMap(merenderoId => {
        return this.movementsService.getOutputsFromMerendero(merenderoId);
      }),
      // cuando lleguen las donaciones, seteamos donaciones y aplicamos filtros
      tap(outputs => {
        this.outputs = outputs;
        this.filteredOutputs = [...outputs];
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