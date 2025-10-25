import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { DonationsInTimeChartComponent } from './charts/donations-in-time-chart/donations-in-time-chart.component';
import { TypeDonorsChartComponent } from './charts/type-donors-chart/type-donors-chart.component';
import { DonationsInTimeMobileChartComponent } from './charts/donations-in-time-mobile-chart/donations-in-time-mobile-chart.component';
import { PeriodComparisonChartComponent } from './charts/period-comparison-chart/period-comparison-chart.component';
import { DonationDashboardResponse } from '../../models/dashboard/donation-dashboard.model';
import { PeriodStats } from '../../models/dashboard/period-stats.model';
import { ComparisonStats } from '../../models/dashboard/comparison-stats.model';
import { DonorAnalysis } from '../../models/dashboard/donor-analysis.model';
import { DonationDateSummary } from '../../models/dashboard/donation-date-summary.model';
import { AuthService } from '../../../user/services/auth.service';
import { DonationService } from '../../services/donation.service';
import { AlertService } from '../../../shared/services/alert.service';
import { DatesService } from '../../../shared/services/dates.service';
import { catchError, finalize, of, tap } from 'rxjs';
import { CurrencyArsPipe } from '../../../shared/pipes/currency-ars.pipe';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-donation-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule,
    PeriodComparisonChartComponent, TypeDonorsChartComponent, DonationsInTimeChartComponent,
    DonationsInTimeMobileChartComponent, CurrencyArsPipe],
  templateUrl: './donation-dashboard.component.html',
  styleUrl: './donation-dashboard.component.css'
})
export class DonationDashboardComponent {
  // Inyección de dependencias
  private readonly authService = inject(AuthService);
  private readonly donationService = inject(DonationService);
  private readonly alertService = inject(AlertService);
  private readonly datesService = inject(DatesService);
  private readonly cdRef = inject(ChangeDetectorRef);

  // Propiedades
  merenderoId = 0; // Merendero del usuario registrado
  isLoading: boolean = false;
  googleChartsLoaded = false;
  isSmallScreen = window.innerWidth < 1100;

  // Dashboard principal
  donationDashboard: DonationDashboardResponse | null = null;

  // Variables desglosadas para el template
  currentPeriod: PeriodStats | null = null;
  previousPeriod: PeriodStats | null = null;
  comparisonStats: ComparisonStats | null = null;
  donorAnalysis: DonorAnalysis | null = null;
  donationDateSummaries: DonationDateSummary[] = [];
  shouldAnimateApplyButton = true;

  startDate?: string;
  endDate?: string;
  groupBy: 'day' | 'week' | 'month' = 'day';
  private resizeTimeout: any;

  // Se cargan las fechas por defecto y se obtiene el merendero del
  // usuario autenticaso
  ngOnInit(): void {
    this.setDefaultDates();
    this.loadMerenderoId();
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
   * Se realiza la llamada al backend con filtros seleccionados 
   **/
  applyFilters() {
    // Validaciones mínimas
    if (!this.merenderoId || !this.startDate || !this.endDate) {
      return;
    }

    this.shouldAnimateApplyButton = false;

    // Calculamos la diferencia en dias entre las dos fechas, y en base a eso definimos si agrupamos los datos
    // en dias, semanas o meses
    this.applyGroupByDiffDays();

    // Realizamos la llamada al backend
    this.fetchDonationDashboardResponse();
  }

  /**
  * Determina si el cambio en el monto donado es positivo
  */
  isAmountChangePositive(): boolean {
    return (this.comparisonStats?.amountDonatedChange?.percentage ?? 0) >= 0;
  }

  /**
   * Determina si el cambio en la cantidad de donaciones es positivo
   */
  isCountChangePositive(): boolean {
    return (this.comparisonStats?.donationCountChange?.percentage ?? 0) >= 0;
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
  * Llamada al backend para conseguir el resumen completo de donaciones.
  **/
  private fetchDonationDashboardResponse(): void {
    this.isLoading = true;

    this.donationService.getDonationDashboard(
      this.merenderoId,
      this.startDate!,
      this.endDate!,
      this.groupBy
    ).pipe(
      // Se ejecuta una vez que el observable termina
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: dashboardResponse => {
        this.updateDashboardData(dashboardResponse);
        this.cdRef.detectChanges();
      },
      error: err => {
        console.error('Error al obtener reporte:', err);
      }
    });
  }

   private updateDashboardData(dashboardResponse: DonationDashboardResponse): void {
    this.donationDashboard = dashboardResponse;
    this.currentPeriod = dashboardResponse.currentPeriod;
    this.previousPeriod = dashboardResponse.previousPeriod;
    this.comparisonStats = dashboardResponse.comparisonStats;
    this.donorAnalysis = dashboardResponse.donorAnalysis;
    this.donationDateSummaries = dashboardResponse.donationDateSummaries;
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

      // Esperar 300ms después del último resize
      this.resizeTimeout = setTimeout(() => {
        this.isSmallScreen = window.innerWidth < 1100;
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

  private loadMerenderoId() {
    this.authService.getMerenderoIdOfUser().pipe(
      tap(merenderoId => {
        this.merenderoId = merenderoId;
        this.applyFilters();
      }),
      catchError(err => {
        console.error('Error obteniendo merenderoId:', err);
        this.alertService.error("Error", "No se pudo obtener el merendero asignado");
        return of(null); // Retornamos null en caso de error
      })
    ).subscribe();
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
    if (this.isSmallScreen) {
      // Lógica para pantallas pequeñas
      if (diffDays <= 5) {
        this.groupBy = 'day';
      } else if (diffDays <= 32) {
        this.groupBy = 'week';
      } else {
        this.groupBy = 'month';
      }
    } else {
      // Lógica para pantallas normales o grandes
      if (diffDays <= 32) {
        this.groupBy = 'day';
      } else if (diffDays < 64) {
        this.groupBy = 'week';
      } else {
        this.groupBy = 'month';
      }
    }
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