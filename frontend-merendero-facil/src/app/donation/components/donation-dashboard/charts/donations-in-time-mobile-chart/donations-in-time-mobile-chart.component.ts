import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { DonationDateSummary } from '../../../../models/dashboard/donation-date-summary.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-donations-in-time-mobile-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="timeline_chart_mobile"></div>
  `
})
export class DonationsInTimeMobileChartComponent {
  @Input() donationDateSummaries: DonationDateSummary[] = [];
  @Input() googleChartsLoaded: boolean = false;
  @Input() groupBy: 'day' | 'week' | 'month' = 'day';

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawTimelineMobileChart();
    }
  }

  /**
   * Se ejecuta cada vez que un input cambia de valor 
   **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawTimelineMobileChart();
    }
  }

  /**
   * Dibuja el gráfico de barras horizontal para mobile.
   * Se asegura de que Google Charts esté cargado y limpia el contenedor antes de dibujar.
   */
  private drawTimelineMobileChart() {
    if (!this.googleChartsLoaded) return;

    const data = this.getData();
    const options = this.getOptions();

    const chartElement = document.getElementById('timeline_chart_mobile');
    if (chartElement) {
      chartElement.innerHTML = '';
      const chart = new google.visualization.BarChart(chartElement);
      chart.draw(data, options);
    }
  }

  /**
   * Genera y devuelve un DataTable con los datos de donaciones.
   * Cada fila contiene: periodo, monto, anotación (monto formateado) y estilo de color.
   */
  private getData() {
    // Crear DataTable
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Periodo');
    data.addColumn('number', 'Monto');
    data.addColumn({ type: 'string', role: 'annotation' });
    data.addColumn({ type: 'string', role: 'style' });

    // Formateador para montos
    const fmt = (v: number) => '$ ' + v.toLocaleString('es-AR', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    });

    // Llenar con datos
    this.donationDateSummaries.forEach(d => {
      data.addRow([d.label, d.amountDonated, fmt(d.amountDonated),'color: #d35400'
      ]);
    });

    return data;
  }

  /**
 * Define y devuelve las opciones de configuración para el gráfico.
 */
  private getOptions() {
    const textStyle = { fontSize: 13, bold: true, color: '#2c3e50', fontName: 'Poppins', auraColor: 'none' };

    return {
      title: 'Donaciones en el tiempo', titleTextStyle: { fontSize: 16, bold: true },
      legend: 'none', bars: 'horizontal', tooltip: { trigger: 'none' },
      annotations: { alwaysOutside: false, textStyle },
      chartArea: { left: 80, top: 20, bottom: 0, width: '100%', height: '80%' },
      height: Math.max(300, this.donationDateSummaries.length * 40), width: '100%',
      hAxis: { textPosition: 'none', gridlines: { color: 'transparent' }, baselineColor: 'transparent', minValue: 0 },
      vAxis: { textStyle }
    };
  }
}