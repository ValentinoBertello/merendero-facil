import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { catchError, map, Observable, of, switchMap, tap } from 'rxjs';
import { SupplyService } from '../../../services/supply.service';
import { AuthService } from '../../../../user/services/auth.service';
import { SupplyResponseDto } from '../../../models/supply/supply-response.model';
import { MovementsService } from '../../../services/movements.service';
import { OutputRequestDto } from '../../../models/movement/output-request.model';
import { AlertService } from '../../../../shared/services/alert.service';
import { InventoryService } from '../../../services/inventory.service';

@Component({
  selector: 'app-add-salida',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule
  ],
  templateUrl: './add-salida.component.html',
  styleUrl: './add-salida.component.css'
})
export class AddSalidaComponent {
  // Inyección de dependencias
  private readonly inventoryService = inject(InventoryService);
  private readonly alertService = inject(AlertService);
  private readonly supplyService = inject(SupplyService);
  private readonly authService = inject(AuthService);
  private readonly movementsService = inject(MovementsService);

  // Propiedades
  formReactivo: FormGroup;
  private merenderoId = 0;
  //Supplies asociados al merendero
  supplies: SupplyResponseDto[] = [];
  isLoadingSave: boolean = false;

  constructor(private router: Router) {
    this.formReactivo = new FormGroup({
      supplyId: new FormControl('', [
        Validators.required
      ]),
      quantity: new FormControl('', [
        Validators.required,
        Validators.min(1)
      ], [this.quantityHigherThanStockValidator()])
    });
  }

  ngOnInit(): void {
    this.loadSupplies();
  }

  onSubmitForm() {
    this.isLoadingSave = true;
    const formValue = this.formReactivo.value;

    const requestDto: OutputRequestDto = {
      merenderoId: this.merenderoId,
      supplyId: formValue.supplyId,
      quantity: formValue.quantity
    }

    this.movementsService.saveOutput(requestDto).subscribe({
      next: () => {
        this.isLoadingSave = false;
        this.alertService.success('Salida registrada con éxito!').then((result) => {
          if (result.isConfirmed) {
            this.router.navigate(['/stock/inventario']);
          }
        });
      },
      error: (error: any) => {
        console.log(error)
        this.alertService.error("Error", "No se ha podido realizar el registro, intenta más tarde por favor");
      }
    })
  }

  /**
   * Se cargan los supplies de este merendero, para que pueda seleccionar uno
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
      }),
      catchError(err => {
        console.error('Error:', err);
        this.alertService.error("Error", "Ocurrió un error inesperado");
        return of([]);
      })
    ).subscribe();
  }

  /**
    * Validador asíncrono que consulta al backend el stock del insumo,
    * y asi poder chequear si la cantidad ingresada por el usuario es válida.
    */
    private quantityHigherThanStockValidator(): AsyncValidatorFn {
      return (control: AbstractControl): Observable<ValidationErrors | null> => {
        if (!control.value) {
          return of(null);
        }
  
        const quantity = control.value;
        const supplyId = this.formReactivo.get('supplyId')?.value;

        return this.inventoryService.getTotalStockBySupply(this.merenderoId, supplyId).pipe(
          map(stock => {
            if (quantity > stock) {
              return {
                quantityHigherThanStockError: true
              };
            } else {
              return null;
            }
          }),
          catchError((error) => {
            console.error('Error', error);
            return of(null);
          })
        );
      };
    }
}