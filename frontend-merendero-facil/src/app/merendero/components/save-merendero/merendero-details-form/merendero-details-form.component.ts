import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export interface MerenderoReactiveFormDto {
  name: string;
  openingTime: string;
  closingTime: string;
  capacity: number;
  daysOpen: string;
}
@Component({
  selector: 'app-merendero-details-form',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './merendero-details-form.component.html',
  styleUrl: './merendero-details-form.component.css'
})
export class MerenderoDetailsFormComponent {

  // Evento con el merendero creado
  @Output() merenderoCreated = new EventEmitter<MerenderoReactiveFormDto>();

  formReactivo: FormGroup;
  // Lista de todos los dias posibles
  diasSemana = ['LUNES', 'MARTES', 'MIÉRCOLES', 'JUEVES', 'VIERNES', 'SÁBADO', 'DOMINGO'];
  capacities = [20, 30, 40, 50, '+60'];

  constructor() {
    this.formReactivo = new FormGroup({
      name: new FormControl('', [
        Validators.required,
        Validators.maxLength(50),
        Validators.minLength(2),
        Validators.pattern(/^\s*[A-Za-zÀ-ÿ0-9]+(?: [A-Za-zÀ-ÿ0-9]+)*\s*$/)
      ]),
      openingTime: new FormControl('', [
        Validators.required
      ]),
      closingTime: new FormControl('', [
        Validators.required
      ]),
      capacity: new FormControl('', [
        Validators.required,
        Validators.min(1),
        Validators.max(1000)
      ]),
      daysOpen: new FormControl('', [
        Validators.required
      ])
    }, { validators: this.timesValidator }
    )
  }

  /**
   * Manejamos la selección de dias que abrirá el merendero
   * **/
  public toggleDay(day: string) {
    // Obtenemos el valor daysOpoen del form actual
    const currentValue = this.formReactivo.get('daysOpen')?.value || '';
    // Lista de días que el usuario YA SELECCIONÓ
    const daysArray = currentValue ? currentValue.split(',') : [];

    // Si el día seleccionado ya se encuentra, devuelve su posición (será mayor a -1)
    // si no lo encuentra, devolverá -1
    const index = daysArray.indexOf(day);

    if (index > -1) {
      daysArray.splice(index, 1); // ELiminamos el dia seleccionado porque ya está
    } else {
      daysArray.push(day); // Agregamos el dia nuevo
    }

    // join(',') toma un array y lo convierte a string separando sus elementos con comas
    this.formReactivo.get('daysOpen')?.setValue(daysArray.join(','));
    this.formReactivo.get('daysOpen')?.markAsTouched();
  }

  /**
   * Preguntamos si el day del parametro ya esta seleccionado
   * **/
  public isDaySelected(day: string): boolean {
    const currentValue = this.formReactivo.get('daysOpen')?.value || '';
    return currentValue.split(',').includes(day);
  }

  /**
   * Aplicamos el valor seleccionado por el usuario, al form reactivo
   * **/
  public selectCapacity(capacity: string | number) {
    let value: number;
    if (capacity === '+60') {
      value = 60;
    } else {
      value = Number(capacity);
    }
    this.formReactivo.get('capacity')?.setValue(value);
    this.formReactivo.get('capacity')?.markAsTouched();
    this.formReactivo.updateValueAndValidity();
  }

  /**
   * Validador de hora "desde" y hora "hasta"
   * **/
  private timesValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const form = control as FormGroup;
    const openingTime = form.get('openingTime')?.value;
    const closingTime = form.get('closingTime')?.value;
    if (openingTime && closingTime) {
      // Convertir horas a minutos totales desde medianoche
      const [openingHours, openingMinutes] = openingTime.split(':').map(Number);
      const [closingHours, closingMinutes] = closingTime.split(':').map(Number);

      const openingTotal = openingHours * 60 + openingMinutes;
      const closingTotal = closingHours * 60 + closingMinutes;
      if (closingTotal > openingTotal) {
        return null; // Horario válido
      }
    }
    return { TimesIncorrects: true }; // Horario inválido
  }

  public get isFormInvalid(): boolean {
    return this.formReactivo.invalid;
  }

  public emitMerendero() {
    if (this.formReactivo.valid) {
      const merendero: MerenderoReactiveFormDto = {
        name: this.formReactivo.get('name')?.value,
        openingTime: this.formReactivo.get('openingTime')?.value,
        closingTime: this.formReactivo.get('closingTime')?.value,
        capacity: this.formReactivo.get('capacity')?.value,
        daysOpen: this.formReactivo.get('daysOpen')?.value
      };

      this.merenderoCreated.emit(merendero);
      this.formReactivo.reset();
    }
  }
}
