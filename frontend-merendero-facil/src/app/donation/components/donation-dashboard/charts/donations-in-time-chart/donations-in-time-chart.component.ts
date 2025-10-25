import { CommonModule } from '@angular/common';
import { Component, inject, Input, SimpleChanges } from '@angular/core';
import { DonationDateSummary } from '../../../../models/dashboard/donation-date-summary.model';
import { DatesService } from '../../../../../shared/services/dates.service';
declare var google: any; // Declarar para usar Google Charts


@Component({
  selector: 'app-donations-in-time-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div id="timeline_chart"></div>
  `
})
export class DonationsInTimeChartComponent {
  private readonly datesService = inject(DatesService);

  @Input() donationDateSummaries: DonationDateSummary[] = [];
  @Input() googleChartsLoaded: boolean = false;
  @Input() groupBy: 'day' | 'week' | 'month' = 'day';

  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawTimelineChart();
    }
  }

  /**
   * Se ejecuta cada vez que un input cambia de valor 
   **/
  ngOnChanges(changes: SimpleChanges): void {
    if (this.googleChartsLoaded) {
      this.drawTimelineChart();
    }
  }

  /**
    * Dibuja el gráfico de barras de los montos donados en el tiempo.
    **/
  private drawTimelineChart() {
    if (!this.googleChartsLoaded) {
      return;
    }

    const data = this.getData();
    const options = this.getOptions();

    const chartElement = document.getElementById('timeline_chart');
    if (chartElement) {
      chartElement.innerHTML = '';
      const chart = new google.visualization.ColumnChart(chartElement);
      chart.draw(data, options);
    }
  }

  /**
   * Construye y retorna un DataTable con las filas.
   **/
  private getData() {
    const data = new google.visualization.DataTable();

    data.addColumn('date', 'Fecha');
    data.addColumn('number', 'Donación');
    data.addColumn({ type: 'string', role: 'tooltip', 'p': { 'html': true } });

    this.donationDateSummaries.forEach(d => {
      const dateObject = this.datesService.formatStringToLocalDate(d.date);
      const tooltipDonation = this.createTooltip(d.amountDonated, '#ee7859', d.label);
      data.addRow([dateObject, d.amountDonated, tooltipDonation]);
    });

    return data;
  }

  /**
 * Crea el HTML para los tooltips del gráfico
 */
  private createTooltip(amount: number, borderColor: string, label: string): string {
    return `
    <div style="font-family:Poppins; padding: 12px; border-left: 4px solid ${borderColor}">
      <strong style="font-size: 15.2px;">${label}</strong><br/>
      <span style="font-size: 15.2px;">${this.formatNumber(amount)}</span>
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
  * Retorna el objeto de opciones para configurar el gráfico (estilos, ejes y anotaciones).
  **/
  private getOptions() {
    const textStyle = { fontSize: 14.8, color: '#2c3e50', fontName: 'Poppins' };

    return {
      title: 'Donaciones en el tiempo',
      titleTextStyle: { ...textStyle, fontSize: 16, bold: true },
      legend: 'none',
      colors: ['#ee7859'],
      vAxis: {
        viewWindow: { min: 1 },
        format: 'currency',
        textStyle: { ...textStyle, bold: false },
        baselineColor: '#46525c',
        gridlines: { color: '#f0f0f0' }
      },
      tooltip: { isHtml: true, ignoreBounds: true },
      chartArea: { left: 93, right: 35, bottom: 40, top: 20, width: '100%', height: '80%' },
      height: window.innerHeight - 430,
      width: '100%',
      hAxis: this.getHaxisOptions()
    };
  }

  /**
 * CONFIGURACIÓN DEL EJE HORIZONTAL
 * Adapta dinámicamente el formato de fechas según el agrupamiento seleccionado
 */
  private getHaxisOptions() {
    const common = {
      textStyle: { bold: false, fontName: 'Poppins', fontSize: 14.8, color: '#2c3e50' },
      baselineColor: '#46525c',
      gridlines: { color: '#f0f0f0' }
    };

    switch (this.groupBy) {
      case 'day': return { ...common, format: 'dd MMM' };
      case 'week': return {
        ...common,
        ticks: this.donationDateSummaries.map(d => ({ v: new Date(d.date), f: d.label })),
        viewWindowMode: 'explicit'
      };
      default: return { ...common, format: 'MMM yyyy' };
    }
  }
}