import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';
import { DonorAnalysis } from '../../../../models/dashboard/donor-analysis.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-type-donors-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="donor_chart"></div>
  `
})
export class TypeDonorsChartComponent {
  @Input() donorAnalysis: DonorAnalysis | null = null;
  @Input() googleChartsLoaded: boolean = false;

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawTypeDonorsChart();
    }
  }

  /**
  * Se ejecuta cada vez que un input cambia de valor 
  **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawTypeDonorsChart();
    }
  }

  /**
    * Dibuja el gráfico de pie comparando los dos tipos de donantes (nuevos y recurrentes). 
    **/
  private drawTypeDonorsChart() {
    if (!this.googleChartsLoaded) return;
    // Preparación de la tabla de datos
    const data = this.getData();
    // Options del gráfico
    const options: any = this.getOptions();

    // Renderizado
    const elem = document.getElementById('donor_chart');
    if (elem) {
      elem.innerHTML = '';
      const chart = new google.visualization.PieChart(elem);
      chart.draw(data, options);
    }
  }

  /**
  * Construye y retorna el DataTable con los tipos de donantes y sus cantidades.
  **/
  private getData() {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Tipo');
    data.addColumn('number', 'Cantidad');
    data.addRows([
      ['Nuevos', this.donorAnalysis?.donorTypeAnalysis.newDonorsCount || 0],
      ['Recurrentes', this.donorAnalysis?.donorTypeAnalysis.recurrentDonorsCount || 0]

    ]);
    return data;
  }

  /**
   * Retorna las opciones de configuración y estilo para el gráfico de torta de donantes.
   **/
  private getOptions() {
    const textStyle = { fontSize: 13.5, color: '#2c3e50', fontName: 'Poppins', auraColor: 'none' };

    return {
      title: 'Tipos de donantes',
      titleTextStyle: { fontSize: 18, bold: true, color: '#46525c', fontName: 'Poppins' },
      pieHole: 0.6, pieSliceText: 'none',
      legend: { position: 'labeled', textStyle: { fontSize: 12 } },
      chartArea: { width: '100%', height: '75%', left: 0, top: 50 },
      tooltip: { text: 'both', textStyle },
      colors: ['#104c9c', '#4bb160'], height: 200, width: '100%'
    };
  }
}
