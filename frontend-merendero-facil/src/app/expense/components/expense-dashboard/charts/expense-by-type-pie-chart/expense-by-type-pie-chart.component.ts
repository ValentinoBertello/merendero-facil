import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { ChartsData } from '../../../../models/dashboard/charts-data.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-expense-by-type-pie-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="chart_types_expenses"></div>
  `
})
export class ExpenseByTypePieChartComponent {
  @Input() chartsData: ChartsData | null = null;
  @Input() googleChartsLoaded: boolean = false;
  @Input() isSemiSmallScreen = window.innerWidth < 1300;

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawExpensesByTypePieChart();
    }
  }

  /**
    * Se ejecuta cada vez que un input cambia de valor 
    **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawExpensesByTypePieChart();
    }
  }

  private drawExpensesByTypePieChart() {
    // Preparar DataTable
    const data = this.getData();
    // Preparar Opciones
    const options = this.getOptions();

    // 5) Dibujar el gráfico en el div “chart_types_expenses”
    const elem = document.getElementById('chart_types_expenses');
    if (elem) {
      const chart = new google.visualization.PieChart(elem);
      chart.draw(data, options);
    }
  }

  /**
  * Construye y retorna un DataTable con las filas.
  **/
  getData() {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'TipoGasto');
    data.addColumn('number', 'TotalMonto');

    const rows: [string, number][] = [];
    this.chartsData?.expensesByType.forEach((item) => {
      rows.push([item.expenseType, item.amount]);
    });
    data.addRows(rows);

    return data;
  }

  /**
  * Retorna el objeto de opciones para configurar el gráfico (estilos, ejes y anotaciones).
  **/
  getOptions() {
  const isSmall = this.isSemiSmallScreen;
  const textStyle = { fontSize: isSmall ? 12.1 : 14.1, color: '#2c3e50', fontName: 'Poppins', auraColor: 'none' };

  return {
    title: 'Distribución de Gastos por Tipo',
    titleTextStyle: { bold: true, ...textStyle },
    legend: { position: 'right', textStyle },
    pieSliceTextStyle: { fontSize: 10.9, fontName: 'Poppins', color: '#ffffff', auraColor: 'none' },
    chartArea: { left: 0, top: 50, width: '100%', height: '75%' },
    slices: {
      0: { color: '#843a8e' }, 1: { color: '#fecf16', textStyle: { color: '#46525c' } },
      2: { color: '#4bb160' }, 3: { color: '#ec5d92' }, 4: { color: '#c1c0c0', textStyle: { color: '#46525c' } }
    },
    tooltip: { trigger: 'none' }, width: '100%', height: isSmall ? 210 : 250
  };
}
}