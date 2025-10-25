import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { SummaryMovementsDto } from '../../../../../models/dashboard/summary-movements.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-entries-pie-chart',
  standalone: true,
  imports: [CommonModule],
  // html:
  template: `
  <div id="entries_chart"></div>
  `
})
export class EntriesPieChartComponent {
  @Input() summaryMovements: SummaryMovementsDto = SummaryMovementsDto.empty();
  @Input() googleChartsLoaded: boolean = false;

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawEntriesChart();
    }
  }

  /**
   * Se ejecuta cada vez que cambien los `@Input()`
   **/
  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['googleChartsLoaded'] && this.googleChartsLoaded) ||
      (changes['summaryMovements'] && this.summaryMovements)) {
      if (this.googleChartsLoaded) {
        this.drawEntriesChart();
      }
    }
  }

  /**
* Dibuja un gráfico de torta pequeño que muestra la proporción
* entre donaciones y compras (tipos de entradas).
*/
  private drawEntriesChart() {
    const data = this.getData();
    const options = this.getOptions();

    // 5) Obtener contenedor y dibujar el PieChart
    const elem = document.getElementById('entries_chart');
    if (elem) {
      const chart = new google.visualization.PieChart(elem);
      chart.draw(data, options);
    }
  }

  /**
   * Construye y formatea los datos para el gráfico
   **/
  private getData() {
    let totalDonacion = 0;
    let totalCompra = 0;

    this.summaryMovements.groupsMovements.forEach(r => {
      totalDonacion += r.entryDonationQty;
      totalCompra += r.entryPurchaseQty;
    });

    // Crear DataTable para el PieChart
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Tipo');
    data.addColumn('number', 'Cantidad');

    // Agregar filas: una para “Donación” y otra para “Compra”
    data.addRows([
      ['Donación', totalDonacion],
      ['Compra', totalCompra]
    ]);

    return data;
  }

  /**
   * Configuración de opciones visuales del gráfico
   **/
  private getOptions() {
    const textStyle = { fontSize: 13.5, color: '#2c3e50', fontName: 'Poppins', auraColor: 'none' };

    return {
      title: '', legend: { position: 'labeled', textStyle: { ...textStyle, color: '#ffffff' } },
      slices: { 0: { color: '#248F79' }, 1: { color: '#7FCBA3' } }, // verde oscuro/claro
      tooltip: { text: 'both', textStyle }, chartArea: { width: '100%', height: '90%', left: 0, top: 10 },
      pieSliceText: 'none', height: 180, width: '100%'
    };
  }
}