import { CommonModule } from '@angular/common';
import { ChartsData } from '../../../../models/dashboard/charts-data.model';
import { Component, Input, SimpleChanges } from '@angular/core';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-expense-by-type-bar-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="chart_types_expenses_barras"></div>
  `
})
export class ExpenseByTypeBarChartComponent {
  @Input() chartsData: ChartsData | null = null;
  @Input() googleChartsLoaded: boolean = false;

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawExpensesByTypeChart();
    }
  }

  /**
    * Se ejecuta cada vez que un input cambia de valor 
    **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawExpensesByTypeChart();
    }
  }

  private drawExpensesByTypeChart() {
    // Mapa de abreviaciones
    const labelMap: Record<string, string> = {
      'Compra de Insumos': 'Insumos',
      'Mantenimiento': 'Mant.',
      'Luz y Gas': 'Luz/Gas',
      'Productos de Limpieza': 'Limpieza'
    };

    // Preparar DataTable
    const data = this.getData(labelMap);
    // Preparar Opciones
    const options = this.getOptions();

    // Renderizado
    const elem = document.getElementById('chart_types_expenses_barras');
    if (elem) {
      elem.innerHTML = '';
      const chart = new google.visualization.ColumnChart(elem);
      chart.draw(data, options);
    }
  }

  /**
  * Construye y retorna un DataTable con las filas y anotaciones (formateadas en moneda local).
  **/
  private getData(labelMap: Record<string, string>) {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Tipo');
    data.addColumn('number', 'Monto');
    data.addColumn({ type: 'string', role: 'annotation' });
    data.addColumn({ type: 'string', role: 'tooltip' });

    this.chartsData?.expensesByType.forEach(item => {
      const montoNum = item.amount;
      const fmt = montoNum.toLocaleString('es-AR', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
      const annotation = `$ ${fmt}`;
      const tooltip = `${item.expenseType}\n$ ${fmt}`;
      const label = labelMap[item.expenseType] ?? item.expenseType;
      data.addRow([label, montoNum, annotation, tooltip]);
    });
    return data;
  }

  /**
  * Retorna el objeto de opciones para configurar el gráfico (estilos, ejes y anotaciones).
  **/
  private getOptions() {
    return {
      title: 'Gastos por Tipo',
      titleTextStyle: { fontSize: 18, bold: true, fontName: 'Poppins', color: '#2c3e50' },
      legend: 'none',
      colors: ['#104c9c'],
      tooltip: { trigger: 'none' },
      annotations: {
        alwaysOutside: true,
        textStyle: { fontSize: 13, bold: true, fontName: 'Poppins', color: '#2c3e50' }
      },
      width: '100%',
      height: 250,
      chartArea: { left: 0, top: 50, bottom: 50, width: '100%', height: '70%' },
      hAxis: { textStyle: { fontSize: 12, bold: true, fontName: 'Poppins', color: '#2c3e50' } },
      vAxis: { textPosition: 'none', baselineColor: '#e0e0e0', gridlines: { color: '#f0f0f0' } }
    };
  }
}