import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxPaginationModule } from 'ngx-pagination';
import { ItemStockDto } from '../../../models/inventory/item-stock.model';
import { LotDto } from '../../../models/inventory/lot.model';

@Component({
  selector: 'app-lotes-info',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxPaginationModule],
  templateUrl: './lotes-info.component.html',
  styleUrl: './lotes-info.component.css'
})
export class LotesInfoComponent {
  // Recibimos el supply seleccionado en "inventario"
  @Input() selectedSupply!: ItemStockDto;
  // Rebimos los lotes del supply
  @Input() lotesModel!: LotDto[];
  
  constructor(
    public activeModal: NgbActiveModal
  ) { }

  closeModal(): void {
    this.activeModal.close('Close click');
  }

  //Función auxiliar para determinar si un producto está próximo a vencer
  isExpiringSoon(expirationDate: string | null): boolean {
    if (!expirationDate) return false;

    const today = new Date();
    const expDate = new Date(expirationDate);
    const diffTime = expDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    return diffDays <= 15 && diffDays >= 0;
  }
}