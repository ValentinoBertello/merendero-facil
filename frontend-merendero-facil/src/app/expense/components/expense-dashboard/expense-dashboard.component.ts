import { ChangeDetectorRef, Component, HostListener, inject } from '@angular/core';
import { ExpenseService } from '../../services/expense.service';
import { AuthService } from '../../../user/services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ExpenseDashboardResponse } from '../../models/dashboard/expense-dashboard-response.model';
import { catchError, finalize, of, tap } from 'rxjs';
import { AlertService } from '../../../shared/services/alert.service';
import { DatesService } from '../../../shared/services/dates.service';
import { ExpensesVsDonationsBarChartComponent } from './charts/expenses-vs-donations-bar-chart/expenses-vs-donations-bar-chart.component';
import { ExpenseByTypeBarChartComponent } from './charts/expense-by-type-bar-chart/expense-by-type-bar-chart.component';
import { ExpenseByTypePieChartComponent } from './charts/expense-by-type-pie-chart/expense-by-type-pie-chart.component';
import { ExpenseBySupplyBarChartComponent } from './charts/expense-by-supply-bar-chart/expense-by-supply-bar-chart.component';
import { ExpenseByTypeMobileBarChartComponent } from './mobile-charts/expense-by-type-mobile-bar-chart/expense-by-type-mobile-bar-chart.component';
import { ExpenseBySupplyMobileBarChartComponent } from './mobile-charts/expense-by-supply-mobile-bar-chart/expense-by-supply-mobile-bar-chart.component';
import { ExpensesVsDonationsTimeLineChartComponent } from './charts/expenses-vs-donations-time-line-chart/expenses-vs-donations-time-line-chart.component';
import { ExpensesVsDonationMobileBarChartComponent } from './mobile-charts/expenses-vs-donation-mobile-bar-chart/expenses-vs-donation-mobile-bar-chart.component';
import { CurrencyArsPipe } from '../../../shared/pipes/currency-ars.pipe';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-expense-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, ExpensesVsDonationsBarChartComponent,
    ExpenseByTypeBarChartComponent, ExpenseByTypeMobileBarChartComponent, ExpenseByTypePieChartComponent,
    ExpenseBySupplyBarChartComponent, ExpenseBySupplyMobileBarChartComponent, ExpensesVsDonationsTimeLineChartComponent,
    ExpensesVsDonationMobileBarChartComponent, CurrencyArsPipe
  ],
  templateUrl: './expense-dashboard.component.html',
  styleUrl: './expense-dashboard.component.css'
})
export class ExpenseDashboardComponent {
  private readonly authService = inject(AuthService);
  private readonly expenseService = inject(ExpenseService);
  private readonly alertService = inject(AlertService)
  private readonly cdRef = inject(ChangeDetectorRef);
  private readonly datesService = inject(DatesService)

  merenderoId = 0; // Merendero del usuario registrado

  googleChartsLoaded = false;
  isLoading: boolean = false;
  isSemiSmallScreen = window.innerWidth < 1300;
  shouldAnimateApplyButton = true;

  startDate?: string;
  endDate?: string;
  groupBy: 'day' | 'week' | 'month' = 'day';
  private resizeTimeout: any;

  // Dashboard principal
  expenseDashboard: ExpenseDashboardResponse | null = null;

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

    this.fetchExpenseDashboardResponse();
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
   * Configura un listener que detecta cuando la ventana cambia de tamaño
   */
  private setupResizeListener(): void {
    window.addEventListener('resize', () => {
      // Cancelar el timeout anterior si existe
      if (this.resizeTimeout) {
        clearTimeout(this.resizeTimeout);
      }

      this.resizeTimeout = setTimeout(() => {
        this.isSemiSmallScreen = window.innerWidth < 1300;
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
    * Llamada al backend para conseguir el resumen completo de gastos.
    **/
  private fetchExpenseDashboardResponse(): void {
    this.isLoading = true;

    this.expenseService.getExpensesDashboard(
      this.merenderoId,
      this.startDate!,
      this.endDate!,
      this.groupBy
    ).pipe(
      // Se ejecuta una vez que el observable termina
      finalize(() => {
        this.isLoading = false;
        //this.drawMobileExpensesVsDonationsChart();
      })
    ).subscribe({
      next: expenseResponse => {
        // Aplicamos datos de resumen a nuestras variables
        this.expenseDashboard = expenseResponse;
        this.cdRef.detectChanges();
      },
      error: err => {
        console.error('Error al obtener reporte:', err);
      }
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
    if (this.isSemiSmallScreen) {
      // Lógica para pantallas pequeñas
      if (diffDays <= 6) {
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
      } else if (diffDays < 62) {
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