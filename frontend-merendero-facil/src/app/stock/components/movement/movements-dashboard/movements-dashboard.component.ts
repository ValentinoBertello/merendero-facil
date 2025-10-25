import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { MovementsBarsMobileChartComponent } from './charts/movements-bars-mobile-chart/movements-bars-mobile-chart.component';
import { MovementsPieChartComponent } from './charts/movements-pie-chart/movements-pie-chart.component';
import { MovementsBarsChartComponent } from './charts/movements-bars-chart/movements-bars-chart.component';
import { EntriesPieChartComponent } from './charts/entries-pie-chart/entries-pie-chart.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SupplyResponseDto } from '../../../models/supply/supply-response.model';
import { SummaryMovementsDto } from '../../../models/dashboard/summary-movements.model';
import { DatesService } from '../../../../shared/services/dates.service';
import { AlertService } from '../../../../shared/services/alert.service';
import { SupplyService } from '../../../services/supply.service';
import { AuthService } from '../../../../user/services/auth.service';
import { MovementsService } from '../../../services/movements.service';
import { catchError, finalize, of, switchMap, tap } from 'rxjs';
import { CurrencyArsPipe } from '../../../../shared/pipes/currency-ars.pipe';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-movements-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule,
    MovementsPieChartComponent, MovementsBarsChartComponent, EntriesPieChartComponent, CurrencyArsPipe,
    MovementsBarsMobileChartComponent],
  templateUrl: './movements-dashboard.component.html',
  styleUrl: './movements-dashboard.component.css'
})
export class MovementsDashboardComponent {
  // Inyección de dependencias
  private readonly datesService = inject(DatesService);
  private readonly alertService = inject(AlertService);
  private readonly supplyService = inject(SupplyService);
  private readonly authService = inject(AuthService);
  private readonly movementsService = inject(MovementsService);
  private cdRef = inject(ChangeDetectorRef);
  
  // Propiedades
  merenderoId = 0; // Merendero del usuario registrado
  supplies: SupplyResponseDto[] = []; // Insumos para el select

  isLoading: boolean = false;
  googleChartsLoaded = false;
  shouldAnimateApplyButton = true;

  // Labels para el html
  unidadMedidaSingular = '';
  unidadMedidaPlural = '';

  // Variables con resumen completo de movimientos
  summaryMovements: SummaryMovementsDto = SummaryMovementsDto.empty();

  // Filtros seleccionados
  startDate?: string;
  endDate?: string;
  selectedSupply: SupplyResponseDto | undefined = undefined;
  groupBy: 'day' | 'week' | 'month' = 'day';
  private resizeTimeout: any;

  isSmallScreen = window.innerWidth < 1312;

  // Se cargab las fechas por defecto y el select de supplies
  ngOnInit(): void {
    this.loadSupplies();
    this.setDefaultDates();
    this.setupResizeListener();
  }

  // Se cargan los gráficos
  ngAfterViewInit(): void {
    if (typeof google !== 'undefined') {
      google.charts.load('current', { packages: ['corechart', 'bar'] });
      google.charts.setOnLoadCallback(() => {

        this.googleChartsLoaded = true;

      });
    } else {
      console.error('Google Charts no está disponible. Asegúrate de incluir el script.');
    }
  }

  /**
 * Se ejecuta cuando el componente se destruye (cuando se sale de la página)
 * Limpia el timeout pendiente para evitar fugas de memoria
 */
  ngOnDestroy(): void {
    // Limpiar el timeout cuando se destruye el componente
    if (this.resizeTimeout) {
      clearTimeout(this.resizeTimeout);
    }
  }

  /**
   * Configura un listener que detecta cuando la ventana cambia de tamaño
   */
  private setupResizeListener(): void {
    window.addEventListener('resize', () => {
      // Cancelar el timeout anterior si existe
      if (this.resizeTimeout) {
        clearTimeout(this.resizeTimeout);
      }

      this.resizeTimeout = setTimeout(() => {
        this.isSmallScreen = window.innerWidth < 1312;
        this.googleChartsLoaded = false;

        this.applyGroupByDiffDays();
        this.applyFilters();
        setTimeout(() => {
          this.googleChartsLoaded = true;
          this.cdRef.detectChanges();
        }, 650);
      }, 250);
    });
  }

  /**
   * Se aplican los filtros:
   *  - Se define el groupBy
   *  - Se definen labels segun insumo seleccionado
   *  - Se realiza la llamada al backend con filtros seleccionados 
   **/
  applyFilters() {
    // Validaciones mínimas
    if (!this.merenderoId || !this.startDate || !this.endDate || this.selectedSupply === null) {
      return;
    }

    this.shouldAnimateApplyButton = false;

    // Calculamos la diferencia en dias entre las dos fechas, y en base a eso definimos si agrupamos los datos
    // en dias, semanas o meses
    this.applyGroupByDiffDays();

    // Ajustar etiquetas de unidad según el insumo seleccionado
    this.setUnitLabels();

    // Realizamos la llamada al backend
    this.fetchSummaryMovements();
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
   * Llamada al backend para conseguir el resumen completo de movimientos.
   **/
  private fetchSummaryMovements(): void {
    this.isLoading = true;

    this.movementsService.getSummaryMovements(
      this.merenderoId,
      this.selectedSupply!.id,
      this.startDate!,
      this.endDate!,
      this.groupBy
    ).pipe(
      // Se ejecuta una vez que el observable termina
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: summary => {
        // Aplicamos datos de resumen a nuestras variables
        this.summaryMovements = summary;
        this.cdRef.detectChanges();
      },
      error: err => {
        console.error('Error al obtener reporte:', err);
      }
    });
  }

  /**
   * Se aplican labels correspondientes segun insumo seleccionado. 
   **/
  private setUnitLabels(): void {
    if (this.selectedSupply?.unit === 'UNIDAD') {
      this.unidadMedidaPlural = 'unidades';
      this.unidadMedidaSingular = 'u.';
    } else if (this.selectedSupply?.unit === 'KG') {
      this.unidadMedidaPlural = 'kilos';
      this.unidadMedidaSingular = 'kg';
    } else {
      this.unidadMedidaPlural = 'litros';
      this.unidadMedidaSingular = 'L';
    }
  }

  /**
   * Se define el `groupBy` segun la diferencia de dias entre `startDate` y  `endDate`.
   * El `groupBy` se pasara como parámetro al backend para que sepa como mandarnos los datos.
   **/
  private applyGroupByDiffDays() {
    if (!this.startDate || !this.endDate) {
      return;
    }
    const diffDays = this.datesService.diffDays(this.startDate, this.endDate);
    if (diffDays <= 10) {
      this.groupBy = 'week';
    } else if (diffDays < 62) {
      this.groupBy = 'week';
    } else {
      this.groupBy = 'month';
    }
  }

  /**
   * Se cargan los supplies de este merendero, para que se pueda seleccionar uno
   **/
  private loadSupplies() {
    this.authService.getMerenderoIdOfUser().pipe(
      // Cuando llegue el merenderoId, enacadenamos la llamada de insumos
      switchMap(merenderoId => {
        this.merenderoId = merenderoId;
        return this.supplyService.getSuppliesFromMerendero(merenderoId);
      }),
      // cuando lleguen los insumos, seteamos
      tap(supplies => {
        this.supplies = supplies;
        this.selectedSupply = supplies[0];
        this.applyFilters();
      }),
      catchError(err => {
        console.error('Error:', err);
        this.alertService.error("Error", "Ocurrió un error inesperado");
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