import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { ExpenseSummaryData } from '../../../../models/dashboard/expense-summary-data.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-expenses-vs-donations-bar-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="bar_chart_don_vs_exp"></div>
  `
})
export class ExpensesVsDonationsBarChartComponent {
  @Input() expenseSummaryData: ExpenseSummaryData | null = null;
  @Input() googleChartsLoaded: boolean = false;

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawExpensesVsDonationsChart();
    }
  }

  /**
    * Se ejecuta cada vez que un input cambia de valor 
    **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawExpensesVsDonationsChart();
    }
  }

  /**
    * Dibuja el gráfico de barras comparando gastos contra donaciones.
    **/
  private drawExpensesVsDonationsChart() {
    if (!this.googleChartsLoaded) return;
    // Preparación de la tabla de datos
    const data = this.getData();
    // Options del gráfico
    const options: any = this.getOptions();

    // Renderizado
    const elem = document.getElementById('bar_chart_don_vs_exp');
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
    data.addColumn('string', 'Tipo');
    data.addColumn('number', 'Monto');
    data.addColumn({ type: 'string', role: 'annotation' });
    data.addColumn({ type: 'string', role: 'style' });

    const fmt = (v: number) => '$ ' + v.toLocaleString('es-AR', { minimumFractionDigits: 0, maximumFractionDigits: 0 });

    const totalExpense = this.expenseSummaryData?.totalExpenseAmount ?? 0;
    const totalDonation = this.expenseSummaryData?.totalDonationAmount ?? 0;

    data.addRows([
      ['Gastos', totalExpense, fmt(totalExpense), 'color: #104c9c'],
      ['Donaciones', totalDonation, fmt(totalDonation), 'color: #ee7859']
    ]);

    return data;
  }

  /**
  * Retorna el objeto de opciones para configurar el gráfico (estilos, ejes y anotaciones).
  **/
  private getOptions() {
  const textStyle = { fontSize: 13, bold: true, color: '#2c3e50', fontName: 'Arial', auraColor: 'none' };
  return {
    legend: 'none', bars: 'horizontal',
    annotations: { alwaysOutside: false, textStyle },
    tooltip: { trigger: 'none' },
    chartArea: { left: 90, top: 0, bottom: 0, width: '100%', height: '80%' },
    height: 85, width: '100%',
    hAxis: { textPosition: 'none', gridlines: { color: 'transparent' }, baselineColor: 'transparent', minValue: 0 },
    vAxis: { textStyle: { ...textStyle, fontName: 'Poppins' } }
  };
}
}