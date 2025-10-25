import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { PeriodStats } from '../../../../models/dashboard/period-stats.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-period-comparison-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="bar_chart_period_comparison"></div>
  `
})
export class PeriodComparisonChartComponent {
  @Input() currentPeriod: PeriodStats | null = null;
  @Input() previousPeriod: PeriodStats | null = null;
  @Input() googleChartsLoaded: boolean = false;

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawPeriodComparisonChart();
    }
  }

  /**
   * Se ejecuta cada vez que un input cambia de valor 
   **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawPeriodComparisonChart();
    }
  }

  /**
    * Dibuja el gráfico de barras comparando recaudado (periodo actual) vs periodo anterior. 
    **/
  private drawPeriodComparisonChart() {
    if (!this.googleChartsLoaded) return;
    // Preparación de la tabla de datos
    const data = this.getData();
    // Options del gráfico
    const options: any = this.getOptions();

    // Renderizado
    const elem = document.getElementById('bar_chart_period_comparison');
    if (elem) {
      elem.innerHTML = '';
      const chart = new google.visualization.BarChart(elem);
      chart.draw(data, options);
    }
  }

  /**
  * Construye y retorna un DataTable con las filas y anotaciones (formateadas en moneda local).
  **/
  private getData() {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Periodo');
    data.addColumn('number', 'Monto');
    data.addColumn({ type: 'string', role: 'annotation' });
    data.addColumn({ type: 'string', role: 'style' });

    // Formateo de moneda local
    const fmt = (v: number) => '$ ' + v.toLocaleString('es-AR', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    })

    // Añadir filas
    data.addRows([
      [
        'Periodo actual',
        this.currentPeriod?.totalAmountDonated ?? 0,
        fmt(this.currentPeriod?.totalAmountDonated ?? 0),
        'color: #d35400'
      ],
      [
        'Periodo Anterior',
        this.previousPeriod?.totalAmountDonated ?? 0,
        fmt(this.previousPeriod?.totalAmountDonated ?? 0),
        'color: #BDC3C7'
      ]
    ]);

    return data;
  }

  /**
  * Retorna el objeto de opciones para configurar el gráfico (estilos, ejes y anotaciones).
  **/
  private getOptions() {
    const textStyle = { fontSize: 15, bold: true, color: '#2c3e50', fontName: 'Arial' };

    return {
      legend: 'none', bars: 'horizontal',
      annotations: { alwaysOutside: false, textStyle },
      tooltip: { trigger: 'none' },
      chartArea: { left: 130, top: 0, bottom: 0, width: '100%', height: '80%' },
      height: 85, width: '100%',
      hAxis: { textPosition: 'none', gridlines: { color: 'transparent' }, baselineColor: 'transparent', minValue: 0 },
      vAxis: { textStyle: { ...textStyle, fontSize: 13, fontName: 'Poppins' } }
    };
  }
}