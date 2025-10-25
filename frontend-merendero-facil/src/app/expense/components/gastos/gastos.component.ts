import { CommonModule } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { ExpenseResponseDto } from '../../models/expense-response.model';
import { DatesService } from '../../../shared/services/dates.service';
import { ExportsService } from '../../../shared/services/exports.service';
import { AlertService } from '../../../shared/services/alert.service';
import { AuthService } from '../../../user/services/auth.service';
import { ExpenseService } from '../../services/expense.service';
import { catchError, of, switchMap, tap } from 'rxjs';
import { CurrencyArsPipe } from '../../../shared/pipes/currency-ars.pipe';
import { WindowService } from '../../../shared/services/window.service';

@Component({
  selector: 'app-gastos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule, CurrencyArsPipe],
  templateUrl: './gastos.component.html',
  styleUrl: './gastos.component.css'
})
export class GastosComponent {
  // Inyección de dependencias
  private readonly datesService = inject(DatesService);
  private readonly exportService = inject(ExportsService);
  private readonly alertService = inject(AlertService);
  private readonly authService = inject(AuthService);
  private readonly expenseService = inject(ExpenseService);
  private readonly windowService = inject(WindowService);

  // Propiedades
  expenses: ExpenseResponseDto[] = [];
  filteredExpenses: ExpenseResponseDto[] = [];
  isLoading: boolean = false;
  shouldAnimateApplyButton = true;

  currentPage: number = 1;
  itemsPerPage: number = 6;

  //Filtros
  startDate?: string;
  endDate?: string;
  searchSupplies = '';
  sortField: string = 'fecha'
  sortDirection: 'asc' | 'desc' = 'desc';
  selectedFilter: string = '';

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.loadGastos();
    this.setDefaultDates();
    this.itemsPerPage = this.windowService.getPageSize();
  }

  // Al cambio de tamaño recalcula cuántos ítems entran por página y vuelve a aplicar los filtros
  @HostListener('window:resize')
  onResize(): void {
    this.itemsPerPage = this.windowService.getPageSize();
  }

  /**
   * Aplica los filtros actuales a la lista de gastos
   */
  applyFilters() {
    this.shouldAnimateApplyButton = false;
    const options = {
      searchTerm: this.searchSupplies,
      startDate: this.startDate,
      endDate: this.endDate,
      sortField: this.sortField,
      sortDirection: this.sortDirection,
      selectedFilter: this.selectedFilter
    };

    this.filteredExpenses = this.expenseService.filterAndSortExpenses(
      this.expenses,
      options
    );
  }

  /**
   * Navega a la página de agregar nuevo gasto
   */
  navigateToAddExpense() {
    this.router.navigate(['/stock/add-expense']);
  }

  /**
   * Alterna la dirección de ordenamiento entre ascendente y descendente
   */
  toggleSortDirection() {
    this.sortDirection = this.sortDirection === 'asc' ?
      'desc' : 'asc';
  }

  /**
   * Reactiva la animación del botón aplicar cuando cambia algún filtro
   */
  onAnyFilterChange(): void {
    if (!this.shouldAnimateApplyButton) {
      this.shouldAnimateApplyButton = true;
    }
  }

  // Exports
  exportExcel() {
    // Transformar cada gasto a un objeto plano que Excel entienda
    const expensesToExport = this.filteredExpenses.map(expense => {
      const fecha = new Date(expense.expenseDate)
        .toLocaleDateString('es-AR');
      const tipo = expense.type;
      let cantidadTexto = '-';
      if (expense.quantity > 0 && expense.unit) {
        const unidad = this.getUnit(expense.unit);
        cantidadTexto = expense.supplyName
          ? `${expense.quantity} ${unidad} de ${expense.supplyName}`
          : `${expense.quantity} ${unidad}`;
      }
      const costoTexto = expense.amount != null
        ? `$${expense.amount.toFixed(2)}`
        : '-';

      return {
        'Fecha': fecha,
        'Tipo de Gasto': tipo,
        'Cantidad': cantidadTexto,
        'Costo': costoTexto
      };
    });

    this.exportService.exportExcel(expensesToExport, 'gastos', 'Gastos');
  }

  async exportPdf() {
    // Configurar datos de la tabla
    const expensesData = this.filteredExpenses.map(expense => {
      let cantidadTexto = '';
      if (expense.quantity > 0 && expense.unit && expense.supplyName) {
        cantidadTexto = `${expense.quantity} ${this.getUnit(expense.unit)} de ${expense.supplyName}`;
      } else if (expense.quantity > 0 && expense.unit) {
        cantidadTexto = `${expense.quantity} ${this.getUnit(expense.unit)}`;
      } else {
        cantidadTexto = '-';
      }
      const costoTexto = expense.amount ? `$${expense.amount.toFixed(2)}` : '-';
      return [
        new Date(expense.expenseDate).toLocaleDateString('es-AR'),
        expense.type,
        cantidadTexto,
        costoTexto
      ];
    });

    const headers = ['Fecha', 'Tipo de Gasto', 'Cantidad', 'Costo'];
    this.exportService.exportPdf(expensesData, headers, 'gastos', 'Reporte de Gastos', this.startDate, this.endDate);
  }

  /**
   * Convierte la unidad abreviada a su forma plural en español
   */
  getUnit(unit: string) {
    if (unit === 'UNIDAD') {
      return 'unidades'
    }
    if (unit === 'KG') {
      return 'kilos'
    }
    if (unit === 'LITRO') {
      return 'litros'
    }
    return ''
  }

  /**
  * Hacemos llamada inicial al backend para obtener los gastos de un merendero.
  **/
  private loadGastos() {
    this.authService.getMerenderoIdOfUser().pipe(
      // Cuando llegue el merenderoId, enacadenamos la llamada de gastos
      switchMap(merenderoId => {
        return this.expenseService.getExpensesFromMerendero(merenderoId);
      }),
      // cuando lleguen los gastos, seteamos y aplicamos filtros
      tap(expenses => {
        this.expenses = expenses;
        this.filteredExpenses = [...expenses];
        this.applyFilters();
      }),
      catchError(err => {
        console.error('Error en loadDonations:', err);
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