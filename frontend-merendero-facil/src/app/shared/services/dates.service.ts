import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DatesService {

  /**
   * Formatea una Date local a string 'YYYY-MM-DD' (sin convertir a UTC).
   **/
  public formatDateLocalToString(date: Date): string {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();

    const mm = month < 10 ? '0' + month : month.toString();
    const dd = day < 10 ? '0' + day : day.toString();

    return `${year}-${mm}-${dd}`;
  }

  /**
   * Parsea una cadena 'YYYY-MM-DD' creando una Date en zona horaria local.
   **/
  public formatStringToLocalDate(str: string): Date {
    const [yyyy, mm, dd] = str.split('-').map(num => parseInt(num, 10));
    return new Date(yyyy, mm - 1, dd); // mes: 0..11
  }

  /**
 * Devuelve un rango de fechas:
 * - Si no recibe parámetros: desde hace un mes hasta hoy.
 * - Si recibe 2 fechas: rango entre esas dos.
 */
  public getDefaultDateRange(start?: Date, end?: Date): { startDate: string, endDate: string } {
    const today = new Date();

    let startDate: Date;
    let endDate: Date;

    if (start && end) {
      // Caso: se pasan 2 fechas
      startDate = start;
      endDate = end;
    } else {
      // Caso: no se pasan fechas
      startDate = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
      endDate = today;
    }

    return {
      startDate: this.formatDateLocalToString(startDate),
      endDate: this.formatDateLocalToString(endDate),
    };
  }

  /**
  * Comprueba si una fecha cae dentro del rango startDate..endDate.
  **/
  public filterByDate(
    date: string,
    startDate?: string,
    endDate?: string): boolean {
    const dateToCheck = new Date(date);
    // todas las fechas vienen como string vienen como strings; formatStringToLocalDate() las transforma a Date
    const start = startDate ? this.formatStringToLocalDate(startDate) : null;
    const end = endDate ? this.formatStringToLocalDate(endDate) : null;

    if (start && dateToCheck < start) {
      return false;
    }
    if (end) {
      const endOfDay = new Date(end);
      endOfDay.setHours(23, 59, 59, 999);
      if (dateToCheck > endOfDay) {
        return false;
      }
    }
    return true;
  }

  /**
  * Devuelve la diferencia en días entre dos fechas (inclusive).
  * Recibe strings en formato 'YYYY-MM-DD'.
  */
  public diffDays(startDate: string, endDate: string): number {
    const start = this.formatStringToLocalDate(startDate);
    const end = this.formatStringToLocalDate(endDate);

    const msPorDia = 1000 * 60 * 60 * 24;
    const diffTime = end.getTime() - start.getTime();
    return Math.floor(diffTime / msPorDia) + 1; // +1 para incluir ambos extremos
  }

}