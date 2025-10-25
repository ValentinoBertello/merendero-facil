import { Component, HostListener, inject } from '@angular/core';
import { AuthService } from '../../../user/services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { DonationService } from '../../services/donation.service';
import { DonationResponseDto } from '../../models/donation-response.model';
import { DatesService } from '../../../shared/services/dates.service';
import { ExportsService } from '../../../shared/services/exports.service';
import { WindowService } from '../../../shared/services/window.service';
import { CurrencyArsPipe } from '../../../shared/pipes/currency-ars.pipe';
import { Page } from '../../models/page.model';


@Component({
  selector: 'app-donations',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule, CurrencyArsPipe],
  templateUrl: './donations.component.html',
  styleUrl: './donations.component.css'
})
export class DonationsComponent {
  // INYECCIÓN DE DEPENDENCIAS
  private readonly windowService = inject(WindowService);
  private readonly authService = inject(AuthService);
  private readonly donationService = inject(DonationService);
  private readonly datesService = inject(DatesService);
  private readonly exportService = inject(ExportsService);
  
  filteredDonations: DonationResponseDto[] = [];
  isLoading: boolean = false;
  shouldAnimateApplyButton = true;
  private merenderoId: number = 0;

  // Filtros
  startDate?: string;
  endDate?: string;
  searchEmail = '';
  sortField: string = 'donationDate';
  sortDirection: 'asc' | 'desc' = 'desc';

  // Paginación
  pageData: Page<DonationResponseDto> | null = null;
  pageSize: number = 5;
  currentPage: number = 0;
  totalPages = 0;
  hasNext = false;
  hasPrev = false;
  pageNumbers: number[] = [];

  ngOnInit(): void {
    this.setDefaultDates();
    this.loadMerenderoId();
  }

  // Al cambio de tamaño recalcula cuántos ítems entran por página y vuelve a aplicar los filtros
  @HostListener('window:resize')
  onResize(): void {
    this.pageSize = this.windowService.getPageSize();
    this.applyFilters();
  }

  /**
   * Aplica filtros actuales y carga donaciones 
   **/
  applyFilters(page: number = 0) {
    this.shouldAnimateApplyButton = false;

    this.isLoading = true;
    this.currentPage = page;

    const filters = this.buildFilters();

    // string para aplicar ordenamiento
    const sortParam = `${this.sortField},${this.sortDirection}`;

    // Llamada al endpoint
    this.donationService.getDonationPagesByFilters(filters, this.currentPage, this.pageSize, sortParam)
      .subscribe({
        next: pageData => {
          this.pageData = pageData;
          this.filteredDonations = pageData.content;
          this.totalPages = pageData.totalPages;
          this.hasNext = !pageData.last;
          this.hasPrev = !pageData.first;
          this.isLoading = false;
          this.updatePageNumbers();
        },
        error: (error) => {
          console.error('Error al obtener donaciones:', error);
          this.isLoading = false;
          alert('Error al cargar las donaciones. Por favor, intente nuevamente.');
        }
      });
  }

  /**
   * Cambia dirección de ordenamiento
   **/
  toggleSortDirection() {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
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
   * Exporta datos a Excel
   **/
  exportExcel() {
    const donationsToExport = this.filteredDonations.map(donation => ({
      'Fecha': new Date(donation.donationDate).toLocaleDateString('es-AR'),
      'Email del donante': donation.userEmail,
      'Monto bruto': `$${donation.grossAmount}`,
      'Comisión MP': `$${donation.mpFee}`,
      'Monto neto': `$${donation.netAmount}`
    }));
    this.exportService.exportExcel(donationsToExport, 'donaciones', 'Donaciones');
  }

  /**
   * Exporta datos a PDF 
   **/
  async exportPdf() {
    const donationData = this.filteredDonations.map(d => [
      new Date(d.donationDate).toLocaleDateString('es-AR'),
      d.userEmail,
      `$${d.grossAmount.toFixed(2)}`,
      `$${d.netAmount.toFixed(2)}`
    ]);

    const headers = ['Fecha', 'Email donante', 'Monto bruto', 'Monto neto'];
    this.exportService.exportPdf(donationData, headers, 'donaciones', 'Reporte de Donaciones', this.startDate, this.endDate);
  }

  /** Avanza a la siguiente página si existe */
  goNext(): void {
    if (this.hasNext) {
      this.applyFilters(this.currentPage + 1);
    }
  }

  /** Retrocede a la página anterior si existe */
  goPrev(): void {
    if (this.hasPrev) {
      this.applyFilters(this.currentPage - 1);
    }
  }

  /**
   * Carga ID del merendero del usuario autenticado
   **/
  private loadMerenderoId() {
    this.authService.getMerenderoIdOfUser().subscribe(merenderoId => {
      this.merenderoId = merenderoId;
      this.applyFilters();
    });
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

  /**
   * Construye objeto de filtros para enviar a la API 
   **/
  private buildFilters() {
    const filters: { merenderoId?: number; sinceDate?: string; untilDate?: string; email?: string; } = {};
    if (this.merenderoId) filters.merenderoId = this.merenderoId;
    if (this.startDate) filters.sinceDate = this.startDate;
    if (this.endDate) filters.untilDate = this.endDate;
    if (this.searchEmail) filters.email = this.searchEmail;
    return filters;
  }

  /**
   * Calcula y establece los botones de paginación visibles centrados en la página actual.
   * Usa -1 como marcador para mostrar puntos suspensivos cuando hay un salto de páginas. 
   **/
  private updatePageNumbers(): void {
    console.log(this.currentPage);
    const pages: number[] = [];
    const maxVisiblePages = 5;
    const total = this.totalPages;

    // Si hay pocas páginas, mostrar todas
    if (total <= maxVisiblePages) {
      for (let i = 0; i < total; i++) {
        pages.push(i);
      }
      this.pageNumbers = pages;
      return;
    }

    let startPage = Math.max(0, this.currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(this.totalPages - 1, startPage + maxVisiblePages - 1);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(0, endPage - maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    // Mostrar puntos y última página si es necesario
    if (endPage < total - 1) {
      if (endPage < total - 2) {
        pages.push(-1); // Puntos si hay gap
      }
      pages.push(total - 1);
    }

    this.pageNumbers = pages;
  }
}