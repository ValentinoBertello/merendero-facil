import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { catchError, of, switchMap, tap } from 'rxjs';
import { SupplyService } from '../../../services/supply.service';
import { AuthService } from '../../../../user/services/auth.service';
import { MovementsService } from '../../../services/movements.service';
import { SupplyResponseDto } from '../../../models/supply/supply-response.model';
import { EntryRequestDto } from '../../../models/movement/entry-request.model';
import { ExpenseRequestDto } from '../../../../expense/models/expense-request.model';
import { SupplyPurchaseService } from '../../../services/supply-purchase.service';
import { PurchaseRequestDto } from '../../../models/purchase/purchase-request.model';
import { AlertService } from '../../../../shared/services/alert.service';

@Component({
  selector: 'app-add-entrada',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule],
  templateUrl: './add-entrada.component.html',
  styleUrl: './add-entrada.component.css'
})
export class AddEntradaComponent {
  // Inyeccion de dependencias
  private readonly alertService = inject(AlertService);
  private readonly supplyService = inject(SupplyService);
  private readonly authService = inject(AuthService);
  private readonly movementsService = inject(MovementsService);
  private readonly purchaseService = inject(SupplyPurchaseService);

  // Propiedades
  formReactivo: FormGroup;
  supplies: SupplyResponseDto[] = []; //Supplies asociados al merendero
  private merenderoId = 0;

  constructor(private router: Router) {
    this.formReactivo = this.initializeForm();
    this.setupEntryTypeListener();
  }

  ngOnInit(): void {
    this.loadSupplies();
  }

  onSubmitForm() {
    if (this.formReactivo.invalid) {
      this.alertService.error('Error', 'Por favor completa todos los campos requeridos');
      return;
    }

    const formValue = this.formReactivo.value;
    const isPurchase = formValue.entryType === 'PURCHASE';

    // Llamamos metodo distintos dependiendo de si nos donaron los insumos
    // O los compramos
    if (isPurchase) {
      this.saveEntryAndExpense();
    } else {
      this.saveOnlyEntry();
    }
  }

  /**
 * Inicializa el formulario reactivo con todos los controles y validadores
 */
  private initializeForm(): FormGroup {
    return new FormGroup({
      entryType: new FormControl('DONATION', [
        Validators.required
      ]),
      supplyId: new FormControl('', [
        Validators.required
      ]),
      quantity: new FormControl('', [
        Validators.required,
        Validators.min(1)
      ]),
      expirationDate: new FormControl('', [
        Validators.required,
        this.noPastDateValidator
      ]),
      cost: new FormControl(1) // sin validadores de momento ya que no sabemos si es una compra o una donación
    });
  }

  /**
 * Configura el listener para cambios en el tipo de entrada para ajustar validadores dinámicamente
 */
  private setupEntryTypeListener(): void {
    this.formReactivo.get('entryType')?.valueChanges.subscribe(value => {
      const costControl = this.formReactivo.get('cost');

      if (value === 'PURCHASE') {
        // Si el entryType es PURCHASE, cost pasa a ser requerido y mínimo 1
        costControl?.setValidators([Validators.required, Validators.min(1)]);
      } else {
        // Si es DONATION (o cualquier otro tipo), me aseguro de limpiar validadores
        costControl?.clearValidators();
      }

      // Cada vez que cambio validadores, actualizo el estado de error/validez
      costControl?.updateValueAndValidity();
    });
  }

  /**
  * Carga los insumos del merendero autenticado desde el backend
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
        alert('Ocurrió un error al cargar insumos.');
        return of([]);
      })
    ).subscribe();
  }

  /**
  * Guarda tanto la entrada de insumos como el gasto asociado para compras
  **/
  private saveEntryAndExpense() {
    const formValue = this.formReactivo.value;

    const entryRequestDto: EntryRequestDto = {
      merenderoId: this.merenderoId,
      supplyId: formValue.supplyId,
      quantity: formValue.quantity,
      entryType: 'PURCHASE',
      expirationDate: formValue.expirationDate
    }

    const expenseRequestDto: ExpenseRequestDto = {
      merenderoId: this.merenderoId,
      amount: formValue.cost,
      typeExpenseId: 1
    }

    const purchaseRequestDto: PurchaseRequestDto = {
      entryRequestDto: entryRequestDto,
      expenseRequestDto: expenseRequestDto
    }
    this.purchaseService.saveSupplyPurchase(purchaseRequestDto).subscribe({
      next: () => {
        this.alertService.success('Entrada registrada con éxito!').then((result) => {
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
  * Guarda solo la entrada de insumos sin gasto asociado para donaciones
  **/
  private saveOnlyEntry() {
    const formValue = this.formReactivo.value;

    const requestDto: EntryRequestDto = {
      merenderoId: this.merenderoId,
      supplyId: formValue.supplyId,
      quantity: formValue.quantity,
      entryType: 'DONATION',
      expirationDate: formValue.expirationDate
    }

    this.movementsService.saveEntry(requestDto).subscribe({
      next: () => {
        this.alertService.success('Entrada registrada con éxito!').then((result) => {
          if (result.isConfirmed) {
            this.router.navigate(['/stock/inventario']);
          }
        });
      },
      error: (error: any) => {
        console.log(error);
        this.alertService.error("Error", "No se ha podido realizar el registro, intenta más tarde por favor");
      }
    })
  }

  /**
  * Valida que la fecha de expiración no sea anterior a la fecha actual
  **/
  private noPastDateValidator(control: FormControl): ValidationErrors | null {
    if (!control.value) {
      // Si está vacío, que lo maneje el Validators.required
      return null;
    }
    const fechaIngresada = new Date(control.value);
    // Ponemos la hora en 00:00 para comparar solo la fecha
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    if (fechaIngresada < hoy) {
      return { pastDate: true };
    }
    return null;
  }
}