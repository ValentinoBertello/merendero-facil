import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { ChartsData } from '../../../../models/dashboard/charts-data.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-expenses-vs-donation-mobile-bar-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="expenses_donations_mobile_chart"></div>
  `
})
export class ExpensesVsDonationMobileBarChartComponent {
  @Input() chartsData: ChartsData | null = null;
  @Input() googleChartsLoaded: boolean = false;
  @Input() groupBy: 'day' | 'week' | 'month' = 'day';

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawExpensesVsDonationsMobileChart();
    }
  }

  /**
    * Se ejecuta cada vez que un input cambia de valor 
    **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawExpensesVsDonationsMobileChart();
    }
  }

  private drawExpensesVsDonationsMobileChart() {
    // Crear DataTable
    const data = this.getData();
    // Crear Opciones del gráfico
    const options = this.getOptions();
    // Renderizado
    const elem = document.getElementById('expenses_donations_mobile_chart');
    if (elem) {
      const chart = new google.visualization.BarChart(elem);
      chart.draw(data, options);
    }
  }

  /**
   * Construye y formatea los datos para el gráfico
   **/
  private getData() {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Periodo');
    data.addColumn('number', 'Gastos');
    data.addColumn({ type: 'string', role: 'annotation' });
    data.addColumn('number', 'Donaciones');
    data.addColumn({ type: 'string', role: 'annotation' });

    const fmt = (n: number) =>
      '$ ' + Math.round(n).toLocaleString('es-AR', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
      });

    this.chartsData?.timeGroupedData.forEach(item => {
      data.addRow([item.label, item.expenseAmount, fmt(item.expenseAmount), item.donationAmount, fmt(item.donationAmount)]);
    });

    return data;
  }

  /**
   * Configuración de opciones visuales del gráfico
   **/
  private getOptions() {
    return {
      title: 'Gastos vs Donaciones',
      titleTextStyle: { fontSize: 14.5, bold: true, color: '#2c3e50', fontName: 'Poppins' },
      legend: 'none', bars: 'horizontal', bar: { groupWidth: '60%', gap: 4.5 },
      annotations: { alwaysOutside: false, textStyle: { 
        fontSize: 12, auraColor: 'none', color: '#2c3e50', bold: true, fontName: 'Poppins' 
      } },
      colors: ['#104c9c', '#ee7859'],
      tooltip: { text: 'both', textStyle: { fontSize: 12, auraColor: 'none', color: '#2c3e50', fontName: 'Poppins' } },
      chartArea: { left: 80, top: 20, bottom: 0, right: 80, width: '100%', height: '80%' },
      height: Math.max(300, (this.chartsData?.timeGroupedData.length ?? 0) * 35), width: '100%',
      hAxis: { textPosition: 'none', gridlines: { color: 'transparent' }, baselineColor: 'transparent', minValue: 0 },
      vAxis: { textStyle: { fontSize: 12, fontName: 'Poppins', color: '#000000ff' } }
    };
  }
}