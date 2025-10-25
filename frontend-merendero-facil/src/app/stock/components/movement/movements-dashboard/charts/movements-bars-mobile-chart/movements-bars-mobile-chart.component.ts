import { CommonModule } from '@angular/common';
import { Component, inject, Input, SimpleChanges } from '@angular/core';
import { SummaryMovementsDto } from '../../../../../models/dashboard/summary-movements.model';
import { DatesService } from '../../../../../../shared/services/dates.service';
declare var google: any; // Declarar para usar Google Charts


@Component({
  selector: 'app-movements-bars-mobile-chart',
  standalone: true,
  imports: [CommonModule],
  // html:
  template: `
  <div id="movements_chart_mobile"></div>
  `
})
export class MovementsBarsMobileChartComponent {
  // Se reciben todos los datos necesarios para construir el gráfico
  @Input() summaryMovements: SummaryMovementsDto = SummaryMovementsDto.empty();
  @Input() googleChartsLoaded: boolean = false;

  @Input() groupBy: 'day' | 'week' | 'month' = 'day';
  @Input() unidadMedidaSingular = '';

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawMovementsBarsMobileChart();
    }
  }

  /**
     * Se ejecuta cada vez que cambien los `@Input()`
     **/
  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['googleChartsLoaded'] && this.googleChartsLoaded) ||
      (changes['summaryMovements'] && this.summaryMovements)) {
      if (this.googleChartsLoaded) {
        this.drawMovementsBarsMobileChart();
      }
    }
  }

  drawMovementsBarsMobileChart() {
    if (!this.googleChartsLoaded || this.summaryMovements.groupsMovements.length === 0) return;
    const data = this.getData();
    const options = this.getOptions();

    const el = document.getElementById('movements_chart_mobile');
    if (!el) return;
    el.innerHTML = '';
    const chart = new google.visualization.BarChart(el);
    chart.draw(data, options);
  }

  /**
   * Construye y retorna un DataTable con las filas.
   **/
  private getData() {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Periodo');
    data.addColumn('number', 'Entrada');
    data.addColumn({ type: 'string', role: 'annotation' });
    data.addColumn('number', 'Salida');
    data.addColumn({ type: 'string', role: 'annotation' });

    // Formato de unidad
    const fmt = (n: number) => `${n} ${this.unidadMedidaSingular}`;

    // Llenar filas
    this.summaryMovements.groupsMovements.forEach(g => {

      data.addRow([g.label, g.entryDonationQty + g.entryPurchaseQty, fmt(g.entryDonationQty + g.entryPurchaseQty),
      g.outputQty, fmt(g.outputQty)]);

    });
  }

  /**
  * Retorna el objeto de opciones para configurar el gráfico (estilos, ejes y anotaciones).
  **/
  private getOptions() {
    const textStyle = { fontSize: 11.5, bold: true, color: '#2c3e50', fontName: 'Poppins', auraColor: 'none' };

    return {
      title: 'Movimientos en el tiempo',
      titleTextStyle: { fontSize: 16, bold: true, fontName: 'Poppins', color: '#2c3e50' },
      legend: 'none', bars: 'horizontal', bar: { groupWidth: '78%' },
      annotations: { alwaysOutside: false, textStyle },
      colors: ['#2ca58d', '#FF6565'], tooltip: { text: 'both', textStyle: { ...textStyle, fontSize: 12 } },
      chartArea: { left: 80, top: 0, bottom: 0, width: '100%', height: '100%' }, height: 430,
      hAxis: { textPosition: 'none', gridlines: { color: 'transparent' }, baselineColor: 'transparent', minValue: 0 },
      vAxis: { textStyle: { ...textStyle, fontSize: 12 } }
    };
  }
}