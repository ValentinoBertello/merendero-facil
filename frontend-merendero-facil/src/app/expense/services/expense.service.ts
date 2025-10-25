import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GlobalService } from '../../shared/services/global-urls.service';
import { ExpenseResponseDto } from '../models/expense-response.model';
import { ExpenseRequestDto } from '../models/expense-request.model';
import { ExpenseTypeDto } from '../models/expense-type.model';
import { DatesService } from '../../shared/services/dates.service';
import { ExpenseDashboardResponse } from '../models/dashboard/expense-dashboard-response.model';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {
  private readonly http = inject(HttpClient);
  private readonly globalUrls = inject(GlobalService);
  private apiUrl = this.globalUrls.apiUrlLocalHost8082;

  /**
  * Guarda un nuevo gasto en el sistema
  **/
  saveExpense(expenseRequestDto: ExpenseRequestDto): Observable<ExpenseResponseDto> {
    return this.http.post<ExpenseResponseDto>(this.apiUrl + "/expenses", expenseRequestDto);
  }

  /**
  * Obtiene todos los gastos de un merendero específico
  **/
  getExpensesFromMerendero(merenderoId: number): Observable<ExpenseResponseDto[]> {
    return this.http.get<ExpenseResponseDto[]>(this.apiUrl + "/expenses/" + merenderoId);
  }

  /**
  * Obtiene la lista de todos los tipos de gasto disponibles
  **/
  getAllExpenseTypes(): Observable<ExpenseTypeDto[]> {
    return this.http.get<ExpenseTypeDto[]>(this.apiUrl + "/expenses/types");
  }

  /**
  * Obtiene datos del dashboard de gastos con filtros por fecha y agrupamiento
  **/
  getExpensesDashboard(merenderoId: number, startDate: string, endDate: string,
    groupBy: string): Observable<ExpenseDashboardResponse> {

    console.log("URL:" + this.apiUrl + "/dashboard/expenses/" + merenderoId +
      "/" + startDate + "/" + endDate + "/group/" + groupBy)

    return this.http.get<ExpenseDashboardResponse>(this.apiUrl + "/dashboard/expenses/" + merenderoId +
      "/" + startDate + "/" + endDate + "/group/" + groupBy);
  }

  // Filtrado
  private readonly datesService = inject(DatesService);

  filterAndSortExpenses(
    expenses: ExpenseResponseDto[],
    filters: {
      searchTerm?: string;
      startDate?: string;
      endDate?: string;
      sortField: string;
      sortDirection: 'asc' | 'desc';
      selectedFilter?: string;
    }
  ): ExpenseResponseDto[] {
    const {
      searchTerm = '',
      startDate = '',
      endDate = '',
      sortField = 'fecha',
      sortDirection = 'desc',
      selectedFilter = ''
    } = filters;

    let filtered = expenses.filter(expense => {
      // Filtro por tipo de gasto
      let typeMatch = true;
      if (selectedFilter) {
        const typeMap: { [key: string]: string } = {
          'insumos': 'Compra de Insumos',
          'luzgas': 'Luz y Gas',
          'mantenimiento': 'Mantenimiento',
          'limpieza': 'Productos de Limpieza',
          'otro': 'Otro'
        };
        typeMatch = expense.type === typeMap[selectedFilter];
      }

      // Filtro por nombre de insumo
      const supplyMatch = searchTerm ?
        expense.supplyName.toLowerCase().includes(searchTerm.toLowerCase()) :
        true;

      // Filtro por fecha
      const dateMatch = this.datesService.filterByDate(
        expense.expenseDate,
        startDate,
        endDate
      );

      return typeMatch && supplyMatch && dateMatch;
    });

    // Ordenamiento
    return filtered.sort((a, b) =>
      this.sortExpenses(a, b, sortField, sortDirection)
    );
  }

  private sortExpenses(
    a: ExpenseResponseDto,
    b: ExpenseResponseDto,
    sortField: string,
    sortDirection: string
  ): number {
    if (sortField === 'fecha') {
      const dateA = new Date(a.expenseDate).getTime();
      const dateB = new Date(b.expenseDate).getTime();
      return sortDirection === 'asc' ? dateA - dateB : dateB - dateA;
    }
    else if (sortField === 'costo') {
      return sortDirection === 'asc' ?
        a.amount - b.amount :
        b.amount - a.amount;
    }
    else if (sortField === 'tipo') {
      return sortDirection === 'asc' ?
        a.type.localeCompare(b.type) :
        b.type.localeCompare(a.type);
    }
    return 0;
  }
}