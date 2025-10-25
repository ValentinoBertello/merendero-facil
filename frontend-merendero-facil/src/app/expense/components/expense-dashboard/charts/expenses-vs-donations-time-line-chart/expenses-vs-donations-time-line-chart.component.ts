import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { ChartsData } from '../../../../models/dashboard/charts-data.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-expenses-vs-donations-time-line-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="chart_lines_donations_expenses"></div>
  `
})
export class ExpensesVsDonationsTimeLineChartComponent {
  @Input() chartsData: ChartsData | null = null;
  @Input() googleChartsLoaded: boolean = false;
  @Input() groupBy: 'day' | 'week' | 'month' = 'day';

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawExpensesVsDonationsTimeLineChart();
    }
  }

  /**
    * Se ejecuta cada vez que un input cambia de valor 
    **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawExpensesVsDonationsTimeLineChart();
    }
  }

  private drawExpensesVsDonationsTimeLineChart() {
    // Crear DataTable
    const data = this.getData();
    // Crear Opciones del gráfico
    const options = this.getOptions();
    // Renderizado
    const elem = document.getElementById('chart_lines_donations_expenses');
    if (elem) {
      const chart = new google.visualization.LineChart(elem);
      chart.draw(data, options);
    }
  }

  /**
   * Construye y formatea los datos para el gráfico
   **/
  private getData() {
    const data = new google.visualization.DataTable();
    data.addColumn('date', 'Fecha');
    data.addColumn('number', 'Gastos');
    data.addColumn({ type: 'string', role: 'tooltip', 'p': { 'html': true } });
    data.addColumn('number', 'Donaciones');
    data.addColumn({ type: 'string', role: 'tooltip', 'p': { 'html': true } });

    this.chartsData?.timeGroupedData.forEach((item) => {
      const date = new Date(item.date);

      const tooltipExpense = this.createTooltip(item.expenseAmount, '#104c9c', item.label);
      const tooltipDonation = this.createTooltip(item.donationAmount, '#ee7859', item.label);

      data.addRow([date, item.expenseAmount, tooltipExpense, item.donationAmount, tooltipDonation]);
    });

    return data;
  }

  /**
 * Crea el HTML para los tooltips del gráfico
 */
  private createTooltip(amount: number, borderColor: string, label: string): string {
    return `
    <div style="font-family:Poppins; padding: 10px; border-left: 4px solid ${borderColor}">
      <strong>${label}</strong><br/>
      <span style="font-size: 13.2px;">${this.formatNumber(amount)}</span>
    </div>`;
  }

  /**
   * Formatea números para mostrarlos en los tooltips
   */
  private formatNumber(value: number): string {
    return `$${value.toLocaleString('es-AR', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    })}`;
  }

  /**
   * Configuración de opciones visuales del gráfico
   **/
  private getOptions() {
    const vh = window.innerHeight;
    let dynamicHeight = vh - 458;

    const options: any = {
      title: 'Gastos vs Donaciones',
      titleTextStyle: { fontSize: 18, bold: true, color: '#2c3e50', fontName: 'Poppins' },
      legend: 'none',
      curveType: 'function',
      pointSize: 5,
      series: { 0: { color: '#104c9c' }, 1: { color: '#ee7859' } },
      vAxis: {
        format: 'short',
        baselineColor: '#46525c',
        gridlines: { color: '#f0f0f0' },
        textStyle: { bold: true, fontName: 'Poppins', fontSize: 12, color: '#2c3e50' }
      },
      tooltip: { isHtml: true, ignoreBounds: true },
      chartArea: { left: 60, top: 50, right: 45, width: '100%', height: '70%' },
      height: dynamicHeight,
      width: "100%"
    };

    options.hAxis = this.getHaxisOptions();
    return options;
  }

  /**
   * CONFIGURACIÓN DEL EJE HORIZONTAL
   * Adapta dinámicamente el formato de fechas según el agrupamiento seleccionado
   */
  private getHaxisOptions() {
    const common = { textStyle: { fontName: 'Poppins', fontSize: 13, color: '#2c3e50' } };

    if (this.groupBy === "week") {
      const ticks = this.chartsData?.timeGroupedData.map(item => ({ v: new Date(item.date), f: item.label }));
      return { ...common, ticks, viewWindowMode: 'explicit' };
    }

    return { ...common, format: this.groupBy === "day" ? 'dd MMM' : 'MMM yyyy' };
  }
}