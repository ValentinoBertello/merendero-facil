import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MerenderoResponseDto } from '../../../models/merendero-response.model';

@Component({
  selector: 'app-merendero-info-panel',
  standalone: true,
  imports: [],
  templateUrl: './merendero-info-panel.component.html',
  styleUrl: './merendero-info-panel.component.css'
})
export class MerenderoInfoPanelComponent {
  @Input() merendero: MerenderoResponseDto | null = null;
  @Input() showInfoPanel: boolean = false;
  @Output() closed = new EventEmitter<void>();

  cerrarPanel() {
    this.closed.emit();
  }

  getDaysArray(): string[] {
    if (!this.merendero) return [];
    return this.merendero.daysOpen.split(',');
  }

  isDayActive(day: string): boolean {
    const today = new Date().toLocaleDateString('es-ES', { weekday: 'long' }).toUpperCase();
    return day.toUpperCase() === today;
  }
}
