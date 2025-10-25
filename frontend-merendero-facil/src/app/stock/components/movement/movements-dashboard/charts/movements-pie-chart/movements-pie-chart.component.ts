import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { SummaryMovementsDto } from '../../../../../models/dashboard/summary-movements.model';
declare const google: any;

@Component({
  selector: 'app-movements-pie-chart',
  standalone: true,
  imports: [CommonModule],
  // html:
  template: `
  <div id="movements_chart_pie"></div>
  `
})
export class MovementsPieChartComponent {
  @Input() summaryMovements: SummaryMovementsDto = SummaryMovementsDto.empty();
  @Input() googleChartsLoaded: boolean = false;

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawMovementsPie();
    }
  }

  /**
   * Se ejecuta cada vez que cambien los `@Input()`
   **/
  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['googleChartsLoaded'] && this.googleChartsLoaded) ||
      (changes['summaryMovements'] && this.summaryMovements)) {
      if (this.googleChartsLoaded) {
        this.drawMovementsPie();
      }
    }
  }

  /**
   * Se carga el gráfico con sus configuraciones.
   **/
  drawMovementsPie() {
    const data = this.getData();
    const options = this.getOptions();
    const pieElement = document.getElementById('movements_chart_pie');
    if (pieElement) {
      const pieChart = new google.visualization.PieChart(pieElement);
      pieChart.draw(data, options);
    }
  }

  /**
   * Construye y retorna un DataTable con las filas.
   **/
  private getData() {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Tipo');
    data.addColumn('number', 'Cantidad');

    data.addRows([
      ['Salida', this.summaryMovements.totalOutput],
      ['Entrada', this.summaryMovements.totalEntry]
    ]);

    return data;
  }

  /**
  * Retorna el objeto de opciones para configurar el gráfico (estilos, ejes y anotaciones).
  **/
  private getOptions() {
    const textStyle = { fontSize: 13.5, color: '#2c3e50', fontName: 'Poppins', auraColor: 'none' };
    return {
      legend: 'none',
      slices: { 0: { color: '#FF6565' }, 1: { color: '#2ca58d' } },
      pieSliceTextStyle: { ...textStyle, fontSize: 15.9, color: '#ffffff' },
      tooltip: { text: 'both', textStyle },
      chartArea: { width: '100%', height: '90%', left: 0, top: 5 },
      height: 180, width: '100%'
    };
  }
}