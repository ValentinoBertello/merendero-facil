import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private defaultConfirmText = 'Aceptar';
  private defaultCancelText = 'Cancelar';

  // Devuelven la promesa de SweetAlert para que el componente pueda encadenar .then/await
  success(title: string, text?: string) {
    return Swal.fire({ title, text, icon: 'success', confirmButtonText: this.defaultConfirmText });
  }

  error(title: string, text?: string) {
    return Swal.fire({ title, text, icon: 'error', confirmButtonText: this.defaultConfirmText });
  }

  info(title: string, text?: string) {
    return Swal.fire({ title, text, icon: 'info', confirmButtonText: this.defaultConfirmText });
  }
}
