import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';

export interface Filters {
  openNow: boolean;
  capacityMin: number | null;
  openEveryDay: boolean;
}
@Component({
  selector: 'app-merendero-filter-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './merendero-filter-panel.component.html',
  styleUrl: './merendero-filter-panel.component.css'
})
export class MerenderoFilterPanelComponent {
  localFilters = {
    openNow: false,
    capacityMin: null as number | null,
    openEveryDay: false
  }

  // Recibimos los filtros actuales desde el padre
  @Input() filters!: Filters;
  
  // Eventos que emite el componente
  @Output() apply = new EventEmitter<any>();
  @Output() close = new EventEmitter<void>();

  ngOnChanges(changes: SimpleChanges) {
    if (changes['filters'] && changes['filters'].currentValue) {
      this.localFilters = { ...changes['filters'].currentValue };
    }
  }

  onApply() {
    this.apply.emit({...this.localFilters});
  }

  onClose() {
    this.apply.emit({...this.localFilters});
  }

  onClear() {
    this.localFilters = {
      openNow: false,
      capacityMin: null,
      openEveryDay: false
    };
  }
}
