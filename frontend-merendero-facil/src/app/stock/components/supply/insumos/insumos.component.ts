import { CommonModule } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { Router, RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { SupplyService } from '../../../services/supply.service';
import { AuthService } from '../../../../user/services/auth.service';
import { SupplyCategoryDto } from '../../../models/supply/supply-category.model';
import { SupplyResponseDto } from '../../../models/supply/supply-response.model';
import { catchError, of, switchMap, tap } from 'rxjs';
import { AlertService } from '../../../../shared/services/alert.service';
import { WindowService } from '../../../../shared/services/window.service';

@Component({
  selector: 'app-insumos',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule],
  templateUrl: './insumos.component.html',
  styleUrl: './insumos.component.css'
})
export class InsumosComponent {
  private readonly windowService = inject(WindowService);
  private readonly alertService = inject(AlertService);
  private readonly supplyService = inject(SupplyService);
  private readonly authService = inject(AuthService);

  // Propiedades
  currentPage: number = 1;
  itemsPerPage: number = 5;
  isLoading: boolean = false;
  private merenderoId: number = 0;

  allCategories: SupplyCategoryDto[] = [];
  supplies: SupplyResponseDto[] = [];
  searchSupplies = '';
  filteredSupplies: SupplyResponseDto[] = [];

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.loadSupplies();
    this.loadCategories();
  }

  // AL CAMBIAR EL TAMAÑO DE LA VENTANA: recalcula cuántos ítems entran por página y vuelve a aplicar los filtros
  @HostListener('window:resize')
  onResize(): void {
    this.itemsPerPage = this.windowService.getPageSize();
    this.applyFilters();
  }

  applyFilters() {
    let filtered = this.supplies.filter(supply => {
      const supplyMatch = this.searchSupplies ? supply.name.toLowerCase().includes(this.searchSupplies.toLowerCase())
        : true;
      return supplyMatch;
    })
    this.filteredSupplies = filtered;
  }
  
  getCategoryName(categoryId: number): string {
    const cat = this.allCategories.find(c => c.id === categoryId);
    return cat ? cat.name : '–';
  }

  deleteSupply(supply: SupplyResponseDto) {
    Swal.fire({
      title: "¡Cuidado!",
      text: "¿Seguro que quieres eliminar el insumo " + supply.name + " de la gestión de stock? Se eliminará todo el stock de este insumo si es que hay.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Aceptar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.supplyService.removeSupplyFromMerendero(this.merenderoId, supply.id).subscribe(idSupply => {
          this.ngOnInit();
        })
      }
    });
  }

  navigateToAddSupply() {
    this.router.navigate(['/stock/add-supply']);
  }

  private loadSupplies() {
    this.isLoading = true;
    this.authService.getMerenderoIdOfUser().pipe(
      // Cuando llegue el merenderoId, enacadenamos la llamada de donaciones
      switchMap(merenderoId => {
        this.merenderoId = merenderoId;
        return this.supplyService.getSuppliesFromMerendero(merenderoId);
      }),
      // cuando lleguen los insumos, seteamos
      tap(supplies => {
        this.supplies = supplies;
        this.filteredSupplies = [...supplies];
        this.applyFilters();
        this.isLoading = false;
      }),
      catchError(err => {
        console.error('Error en laodSupplies:', err);
        this.alertService.error("Ocurrió un error inesperado");
        return of([]);
      })
    ).subscribe();
  }

  private loadCategories() {
    this.supplyService.getSupplyCategories().subscribe({
      next: (categories) => {
        this.allCategories = categories;
      },
      error: (err) => {
        console.error('Error al obtener categories:', err);
        this.alertService.error("Ocurrió un error inesperado");
      }
    });
  }
}