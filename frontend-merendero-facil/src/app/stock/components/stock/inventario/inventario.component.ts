import { CommonModule } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LotesInfoComponent } from '../lotes-info/lotes-info.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { ItemStockDto } from '../../../models/inventory/item-stock.model';
import { WindowService } from '../../../../shared/services/window.service';
import { AlertService } from '../../../../shared/services/alert.service';
import { ExportsService } from '../../../../shared/services/exports.service';
import { InventoryService } from '../../../services/inventory.service';
import { AuthService } from '../../../../user/services/auth.service';
import { catchError, of, switchMap, tap } from 'rxjs';


@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule],
  templateUrl: './inventario.component.html',
  styleUrl: './inventario.component.css'
})
export class InventarioComponent {
  // Inyección de dependencias
  private readonly windowService = inject(WindowService);
  private readonly alertService = inject(AlertService);
  private readonly exportService = inject(ExportsService);
  private readonly stockService = inject(InventoryService);
  private readonly authService = inject(AuthService);

  // Propiedades
  suppliesStock: ItemStockDto[] = [];
  filteredItemStock: ItemStockDto[] = [];
  private merenderoId = 0;
  isLoading: boolean = false;
  shouldAnimateApplyButton = true;
  isLoadingLotes = false;

  // Filtros
  sortField: string = 'stock';
  sortDirection: 'asc' | 'desc' = 'desc';
  searchSupplies = '';
  selectedFilter: string = '';

  // Paginación
  currentPage: number = 1;
  itemsPerPage: number = 5;

  constructor(private router: Router, private modal: NgbModal) { }

  ngOnInit(): void {
    this.getStockInventory();
  }

  @HostListener('window:resize')
  onResize(): void {
    this.itemsPerPage = this.windowService.getPageSize();
    this.applyFilters();
  }

  /**
  * Aplica los filtros actuales a la lista de salidas de insumos
  **/
  applyFilters() {
    this.shouldAnimateApplyButton = false;
    const options = {
      searchTerm: this.searchSupplies,
      selectedFilter: this.selectedFilter as any,
      sortField: this.sortField as any,
      sortDirection: this.sortDirection
    };

    this.filteredItemStock = this.stockService.filterAndSortItemsStock(this.suppliesStock, options);
  }

  /**
   * Abre el modal para visualizar los lotes de un insumo específico
   **/
  openLotesModal(supply: ItemStockDto) {
    this.isLoadingLotes = true;

    // Obtener los lotes del backend
    this.stockService.getLotsBySupply(this.merenderoId, supply.supplyId).subscribe({
      next: (lotes) => {
        lotes = lotes;
        this.isLoadingLotes = false;
        console.log("lotes: " + lotes.at(0)?.currentQuantity)
        //Abrir modal
        const modalRef = this.modal.open(LotesInfoComponent, { size: 'lg', keyboard: false });
        modalRef.componentInstance.lotesModel = lotes;
        modalRef.componentInstance.selectedSupply = supply;
      },
      error: (err) => {
        console.error('Error al obtener lotes:', err);
        this.alertService.error("Ocurrió un error inesperado");
        this.isLoadingLotes = false;
      }
    });
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
   * Navega a la página de agregar nuevo insumo al inventario
   **/
  navigateToAddSupply() {
    this.router.navigate(['/stock/add-supply']);
  }

  /**
   * Verifica si un insumo está próximo a vencer (15 días o menos)
   */
  isExpiringSoon(expirationDate: string | null): boolean {
    if (!expirationDate) return false;
    const today = new Date();
    const expDate = new Date(expirationDate);
    const diffTime = expDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    return diffDays <= 15 && diffDays >= 0;
  }

  /**
   * Alterna la dirección de ordenamiento entre ascendente y descendente
   */
  toggleSortDirection() {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
  }

  // Exports
  exportExcel() {
    // Formatear los datos para Excel
    const inventoryToExport = this.filteredItemStock.map(item => ({
      'Insumo': item.supplyName,
      'Categoría': item.category,
      'Stock actual': item.totalStock,
      'Unidad': item.unit,
      'Stock mínimo': item.minQuantity,
      'Próximo vencimiento': item.nextExpiration ? new Date(item.nextExpiration).toLocaleDateString() : '-',
      'Estado': this.getStockStatus(item)
    }));

    this.exportService.exportExcel(inventoryToExport, 'Inventario', 'Inventario');
  }

  async exportPdf() {
    // Convertimos el array de objetos filteredItemStock a un array de arrays
    const inventoryData = this.filteredItemStock.map(item => [
      item.supplyName,
      item.category,
      item.totalStock.toString(),
      item.unit,
      item.nextExpiration ? new Date(item.nextExpiration).toLocaleDateString('es-AR') : '-',
      this.getStockStatus(item)
    ]);
    const headers = ['Insumo', 'Categoría', 'Stock Actual', 'Unidad', 'Próximo Vto.', 'Estado'];
    this.exportService.exportPdf(inventoryData, headers, 'Inventario', 'Reporte de Inventario');
  }

  /**
   * Carga el inventario completo del merendero autenticado desde el backend
   */
  private getStockInventory() {
    this.authService.getMerenderoIdOfUser().pipe(
      switchMap(merenderoId => {
        this.merenderoId = merenderoId;
        return this.stockService.getStockInventoryFromMerendero(merenderoId);
      }),
      // cuando lleguen las items de stock, seteamos y aplicamos filtros
      tap(items => {
        this.suppliesStock = items;
        this.filteredItemStock = [...items];
        this.applyFilters();
      }),
      catchError(err => {
        console.error('Error:', err);
        this.alertService.error("Ocurrió un error inesperado");
        return of([]);
      })
    ).subscribe();
  }

  /**
   * Determina el estado del stock basado en cantidad y fecha de vencimiento
   */
  private getStockStatus(item: ItemStockDto): string {
    const status = [];
    if (item.totalStock < item.minQuantity) {
      status.push('STOCK BAJO');
    }
    if (this.isExpiringSoon(item.nextExpiration)) {
      status.push('VENCIMIENTO CERCANO');
    }
    return status.length > 0 ? status.join(', ') : 'OK';
  }
}