import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { ChartsData } from '../../../../models/dashboard/charts-data.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-expense-by-supply-bar-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="chart_expenses_of_supplies"></div>
  `
})
export class ExpenseBySupplyBarChartComponent {
  @Input() chartsData: ChartsData | null = null;
  @Input() googleChartsLoaded: boolean = false;

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawExpensesBySupplyChart();
    }
  }

  /**
    * Se ejecuta cada vez que un input cambia de valor 
    **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawExpensesBySupplyChart();
    }
  }

  /**
   * Renderiza el gráfico de columnas de gastos por insumo 
   **/
  drawExpensesBySupplyChart() {
    // Crear DataTable
    const data = this.getData();
    // Crear Opciones del gráfico
    const options = this.getOptions();
    // Renderizado
    const elem = document.getElementById('chart_expenses_of_supplies');
    if (elem) {
      const chart = new google.visualization.ColumnChart(elem);
      chart.draw(data, options);
    }
  }

  /**
   * Construye y formatea los datos para el gráfico
   **/
  private getData() {
    const data = new google.visualization.DataTable();

    data.addColumn('string', 'Insumo');
    data.addColumn('number', 'TotalGasto');
    data.addColumn({ type: 'string', role: 'annotation' });
    data.addColumn({ type: 'string', role: 'tooltip' });

    this.chartsData?.expensesBySupply.forEach(item => {
      const monto = Math.round(item.amount);
      const fmt = monto.toLocaleString('es-AR', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
      data.addRow([
        item.supplyName,
        monto,
        `$ ${fmt}`,
        `${item.supplyName}\n$ ${fmt}`
      ]);
    });

    return data;
  }

  /**
   * Configuración de opciones visuales del gráfico
   **/
  private getOptions() {
    const ts = { fontSize: 13, fontName: 'Poppins', color: '#2c3e50' };

    return {
      title: 'Gastos en Insumos',
      titleTextStyle: { ...ts, fontSize: 18, bold: true },
      legend: 'none', colors: ['#843a8e'],
      tooltip: { trigger: 'none' },
      annotations: { alwaysOutside: true, textStyle: { ...ts, bold: true } },
      width: '100%', height: 250,
      chartArea: { left: 0, top: 50, bottom: 50, width: '100%', height: '70%' },
      hAxis: { textStyle: { ...ts, fontSize: 10.8, bold: true } },
      vAxis: { textPosition: 'none', baselineColor: '#e0e0e0', gridlines: { color: '#f0f0f0' } }
    };
  }
}