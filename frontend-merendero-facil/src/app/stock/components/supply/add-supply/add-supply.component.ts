import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { SupplyService } from '../../../services/supply.service';
import { AuthService } from '../../../../user/services/auth.service';
import { SupplyCategoryDto } from '../../../models/supply/supply-category.model';
import { SupplyRequestDto } from '../../../models/supply/supply-request.model';
import { AlertService } from '../../../../shared/services/alert.service';

@Component({
  selector: 'app-add-supply',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './add-supply.component.html',
  styleUrl: './add-supply.component.css'
})
export class AddSupplyComponent {
  // Inyección de dependencias
  private readonly alertService = inject(AlertService);
  private readonly supplyService = inject(SupplyService);
  private readonly authService = inject(AuthService);

  // Propiedades
  private merenderoId: number = 0;
  formReactivo: FormGroup;
  categories: SupplyCategoryDto[] = [];

  // Constructor
  constructor(private router: Router, private fb: FormBuilder) {
    this.formReactivo = this.fb.group({
      name: ['', Validators.required],
      unit: ['UNIDAD', Validators.required],
      stockMin: ['', [Validators.required, Validators.min(1)]],
      categoryId: ['1', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
    this.loadMerenderoId();
  }

  /**
  * Guarda un nuevo insumo en el sistema después de validar el formulario
  **/
  saveSupply() {
    if (this.formReactivo.invalid) {
      this.formReactivo.markAllAsTouched();
      return;
    }

    const request = this.buildSupplyRequest();

    this.supplyService.saveSupply(request, this.merenderoId).subscribe({
      next: () => {
        this.alertService.success('Insumo creado correctamente').then((result) => {
          if (result.isConfirmed) {
            this.router.navigate(['/stock/insumos']);
          }
        });
      },
      error: (error: any) => {
        console.log(error)
        this.alertService.error("Error", "Ocurrió un error al realizar el registro");
      }
    });
  }

  /**
  * Construye el objeto de solicitud para crear un nuevo insumo
  **/
  private buildSupplyRequest(): SupplyRequestDto {
    return {
      name: this.formReactivo.value.name,
      unit: this.formReactivo.value.unit,
      minQuantity: this.formReactivo.value.stockMin,
      lastAlertDate: '1999-06-15',
      supplyCategoryId: Number(this.formReactivo.value.categoryId)
    };
  }

  /**
  * Carga el ID del merendero asociado al usuario autenticado
  **/
  private loadMerenderoId() {
    this.authService.getMerenderoIdOfUser().subscribe({
      next: (merenderoId) => {
        this.merenderoId = merenderoId;
      },
      error: (err) => {
        console.error('Error al obtener el merendero del manager:', err);
        this.alertService.error("Error", "Ocurrió un error inesperado");
      }
    });
  }

  /**
  * Carga las categorías de insumos disponibles desde el backend
  **/
  private loadCategories() {
    this.supplyService.getSupplyCategories().subscribe({
      next: (categories) => this.categories = categories,
      error: (err) => console.error('Error cargando categorías:', err)
    });
  }
}