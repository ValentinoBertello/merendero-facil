import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'currencyArs',
  standalone: true
})
export class CurrencyArsPipe implements PipeTransform {
  transform(
    value: number | string | null | undefined,
    currencySymbol: string = '$',
    showDecimals: boolean = true): string {
    if (value === null || value === undefined || value === '') return '';

    const raw = typeof value === 'number' ? value.toString() : value.toString().trim();

    // Manejo de signo negativo
    const negative = raw.startsWith('-');
    const unsigned = negative ? raw.slice(1) : raw;

    // Separar parte entera y decimales (acepta tanto '.' como ',' como separador decimal)
    const [intPart, decPart] = unsigned.split(/[.,]/);

    // Insertar separador de miles '.' en la parte entera
    const intFormatted = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

    if (!showDecimals) {
      return `${currencySymbol}${negative ? '-' : ''}${intFormatted}`;
    }
    
    // Usamos coma como separador decimal
    const formatted = decPart !== undefined ? `${intFormatted},${decPart}` : intFormatted;
    return `${currencySymbol}${negative ? '-' : ''}${formatted}`;
  }
}