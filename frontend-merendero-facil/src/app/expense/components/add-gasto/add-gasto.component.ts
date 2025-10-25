import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';
import { catchError, of, switchMap, tap } from 'rxjs';
import { SupplyResponseDto } from '../../../stock/models/supply/supply-response.model';
import { ExpenseTypeDto } from '../../models/expense-type.model';
import { AlertService } from '../../../shared/services/alert.service';
import { SupplyService } from '../../../stock/services/supply.service';
import { AuthService } from '../../../user/services/auth.service';
import { ExpenseService } from '../../services/expense.service';
import { SupplyPurchaseService } from '../../../stock/services/supply-purchase.service';
import { ExpenseRequestDto } from '../../models/expense-request.model';
import { EntryRequestDto } from '../../../stock/models/movement/entry-request.model';
import { PurchaseRequestDto } from '../../../stock/models/purchase/purchase-request.model';

@Component({
  selector: 'app-add-gasto',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule],
  templateUrl: './add-gasto.component.html',
  styleUrl: './add-gasto.component.css'
})
export class AddGastoComponent {
  // Inyeccion de dependencias
  private readonly alertService = inject(AlertService);
  private readonly supplyService = inject(SupplyService);
  private readonly authService = inject(AuthService);
  private readonly expenseService = inject(ExpenseService);
  private readonly purchaseService = inject(SupplyPurchaseService);

  // Propiedades
  formReactivo: FormGroup;
  private merenderoId = 0;
  supplies: SupplyResponseDto[] = []; //Supplies asociados al merendero
  expenseTypes: ExpenseTypeDto[] = []; //Tipos de gasto
  showSupplyFields = false;

  // Inicializamos formulario
  constructor(private router: Router) {
    this.formReactivo = this.initializeForm();
  }

  ngOnInit(): void {
    this.loadExpenseTypes();
    this.loadSupplies();

    // Suscribirse a cambios en el tipo de gasto, para ver si es gasto de insumos o de otra cosa
    this.formReactivo.get('typeExpenseId')!.valueChanges
      .subscribe(value => {
        const isSupplyPurchase = String(value) === '1';
        this.showSupplyFields = isSupplyPurchase;

        // En caso de de ser gasto aplicamos validadores a los valores: supplyId, quantity y expirationDate
        this.handleSupplyValidators(isSupplyPurchase);
      });
  }

  /**
   * Maneja el envío del formulario validando y redirigiendo al método correspondiente
   */
  onSubmitForm() {
    if (this.formReactivo.invalid) {
      this.alertService.error("Error", "Completa todos los campos");
      return;
    }
    // Llamamos metodo distintos dependiendo de si el gasto es de insumos
    // o de agua, luz, etc
    if (this.showSupplyFields) {
      this.saveExpenseAndEntry();
    } else {
      this.saveOnlyExpense();
    }
  }

  /**
   * Inicializa el formulario reactivo con sus controles y validadores
   */
  private initializeForm(): FormGroup {
    return new FormGroup({
      cost: new FormControl(0, [
        Validators.required,
        Validators.min(1)
      ]),
      typeExpenseId: new FormControl('', [
        Validators.required
      ]),
      supplyId: new FormControl(''),
      quantity: new FormControl(''),
      expirationDate: new FormControl('')
    });
  }

  /**
  * Carga los tipos de gasto disponibles desde el servicio
  */
  private loadExpenseTypes() {
    this.expenseService.getAllExpenseTypes().subscribe({
      next: (typesR) => {
        this.expenseTypes = typesR;
      },
      error: (err) => {
        console.error('Error al obtener el merendero:', err);
        this.alertService.error("Error", "Ocurrió un error inesperado");
      }
    });
  }

  /**
  * Carga los insumos del merendero autenticado
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
  * Guarda tanto el gasto como la entrada de insumos asociada
  **/
  private saveExpenseAndEntry() {
    const formValue = this.formReactivo.value;

    const expenseRequestDto: ExpenseRequestDto = {
      merenderoId: this.merenderoId,
      amount: formValue.cost,
      typeExpenseId: 1
    }

    const entryRequestDto: EntryRequestDto = {
      merenderoId: this.merenderoId,
      supplyId: formValue.supplyId,
      quantity: formValue.quantity,
      entryType: 'PURCHASE',
      expirationDate: formValue.expirationDate
    }

    const purchaseRequestDto: PurchaseRequestDto = {
      entryRequestDto: entryRequestDto,
      expenseRequestDto: expenseRequestDto
    }

    this.purchaseService.saveSupplyPurchase(purchaseRequestDto).subscribe({
      next: () => {
        this.alertService.success('Gasto registrado con éxito!').then((result) => {
          if (result.isConfirmed) {
            this.router.navigate(['stock/movimientos/gastos']);
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
   * Guarda solo el gasto sin asociar a entrada de insumos
   **/
  private saveOnlyExpense() {
    const formValue = this.formReactivo.value;

    const expenseRequestDto: ExpenseRequestDto = {
      merenderoId: this.merenderoId,
      amount: formValue.cost,
      typeExpenseId: formValue.typeExpenseId
    }

    this.expenseService.saveExpense(expenseRequestDto).subscribe({
      next: () => {
        this.alertService.success('Gasto registrado con éxito!').then((result) => {
          if (result.isConfirmed) {
            this.router.navigate(['stock/movimientos/gastos']);
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
  * Valida que la fecha no sea anterior al día actual
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

  /** 
  * Ajusta dinámicamente los validadores según el tipo de gasto seleccionado
  **/
  private handleSupplyValidators(isSupplyPurchase: boolean) {
    const supplyControl = this.formReactivo.get('supplyId')!;
    const quantityControl = this.formReactivo.get('quantity')!;
    const expirationControl = this.formReactivo.get('expirationDate')!;

    if (isSupplyPurchase) {

      supplyControl.setValidators([Validators.required]);
      quantityControl.setValidators([Validators.required, Validators.min(1)]);
      expirationControl.setValidators([Validators.required, this.noPastDateValidator]);

    } else {
      supplyControl.clearValidators();
      quantityControl.clearValidators();
      expirationControl.clearValidators();

      supplyControl.reset();
      quantityControl.reset();
      expirationControl.reset();
    }
  }
}