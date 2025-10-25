import { CommonModule } from '@angular/common';
import { Component, inject, Input, SimpleChanges } from '@angular/core';
import { SummaryMovementsDto } from '../../../../../models/dashboard/summary-movements.model';
import { DatesService } from '../../../../../../shared/services/dates.service';
import { SupplyResponseDto } from '../../../../../models/supply/supply-response.model';
declare var google: any; // Declarar para usar Google Charts

@Component({
  selector: 'app-movements-bars-chart',
  standalone: true,
  imports: [CommonModule],
  // html:
  template: `
  <div id="movements_chart"></div>
  `
})
export class MovementsBarsChartComponent {
  private readonly datesService = inject(DatesService);

  // Recibimos todos los datos necesarios para construir el gráfico
  @Input() summaryMovements: SummaryMovementsDto = SummaryMovementsDto.empty();
  @Input() googleChartsLoaded: boolean = false;

  @Input() unidadMedidaSingular = '';
  @Input() selectedSupply: SupplyResponseDto | undefined = undefined;
  @Input() groupBy: 'day' | 'week' | 'month' = 'day';


  ngAfterViewInit(): void {
    // Si ya está google charts listo y tenemos datos, dibujamos
    if (this.googleChartsLoaded) {
      this.drawMovementsBars();
    }
  }

  /**
     * Se ejecuta cada vez que cambien los `@Input()`
     **/
  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['googleChartsLoaded'] && this.googleChartsLoaded) ||
      (changes['summaryMovements'] && this.summaryMovements)) {
      if (this.googleChartsLoaded) {
        this.drawMovementsBars();
      }
    }
  }

  /*
   Dibuja el gráfico de barras apiladas usando la lista `groups` recibida del backend.
 */
  private drawMovementsBars() {
    const data = this.getData();

    // Crear un NumberFormatter para añadir "u.", "kg", "L"
    const formatter = new google.visualization.NumberFormat({
      suffix: ' ' + this.unidadMedidaSingular
    });

    // Aplicarlo a la columna “Entrada”
    formatter.format(data, 1);
    // Aplicarlo a la columna “Salida”
    formatter.format(data, 2);

    const options = this.getOptions();

    const chartElement = document.getElementById('movements_chart');
    if (chartElement) {
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
    data.addColumn('number', 'Entrada');
    data.addColumn({ type: 'string', role: 'tooltip', 'p': { 'html': true } });
    data.addColumn('number', 'Salida');
    data.addColumn({ type: 'string', role: 'tooltip', 'p': { 'html': true } });

    const rows: any[] = [];
    this.summaryMovements.groupsMovements.forEach(g => {
      const dateObject = this.datesService.formatStringToLocalDate(g.date);

      const tooltipEntry = this.createTooltip(g.entryDonationQty + g.entryPurchaseQty, '#2ca58d', g.label);
      const tooltipOutput = this.createTooltip(g.outputQty, '#FF6565', g.label);

      rows.push([dateObject, g.entryDonationQty + g.entryPurchaseQty, tooltipEntry, g.outputQty, tooltipOutput]);
    });

    data.addRows(rows);

    return data;
  }

  /**
  * Retorna el objeto de opciones para configurar el gráfico (estilos, ejes y anotaciones).
  **/
  private getOptions() {
    const vh = window.innerHeight;
    const dynamicHeight = vh - 234;

    const textStyle = { fontSize: 12, bold: true, fontName: 'Poppins', color: '#2c3e50' };
    const options: any = {
      title: `Movimientos de Insumo de ${this.selectedSupply?.name}`,
      titleTextStyle: { fontSize: 16, bold: true, color: '#2c3e50', fontName: 'Poppins' },
      isStacked: false,
      legend: { position: 'bottom', textStyle },
      series: { 0: { color: '#2ca58d' }, 1: { color: '#FF6565' } },
      vAxis: {
        title: `Cantidad (${this.unidadMedidaSingular})`,
        baselineColor: '#e0e0e0',
        gridlines: { color: '#f0f0f0' },
        textStyle,
        titleTextStyle: { fontName: 'Poppins', fontSize: 14, color: '#2c3e50' }
      },
      tooltip: { isHtml: true, ignoreBounds: true },
      chartArea: { left: 80, top: 20, width: '100%', height: '70%' },
      height: dynamicHeight
    }
    
    options.hAxis = this.getHaxisOptions();
    return options;
  }

  /**
 * Crea el HTML para los tooltips del gráfico
 */
  private createTooltip(qty: number, borderColor: string, label: string): string {
    return `
    <div style="font-family:Poppins; padding: 12px; border-left: 4px solid ${borderColor}">
      <strong>${label}</strong><br/>
      <span style="font-size: 14.2px;">${qty} ${this.unidadMedidaSingular}</span>
    </div>`;
  }

  /**
 * CONFIGURACIÓN DEL EJE HORIZONTAL
 * Adapta dinámicamente el formato de fechas según el agrupamiento seleccionado
 */
  private getHaxisOptions(): any {
    const common = {
      baselineColor: '#e0e0e0', gridlines: { color: '#f0f0f0' },
      textStyle: { fontSize: 12, bold: true, fontName: 'Poppins', color: '#2c3e50' },
      titleTextStyle: { fontName: 'Poppins', fontSize: 14, color: '#2c3e50' }
    };

    if (this.groupBy === "week") {
      const ticks = this.summaryMovements.groupsMovements.map(g => ({ v: new Date(g.date), f: g.label }));
      return { ...common, ticks, viewWindowMode: 'explicit' };
    }

    return { ...common, format: this.groupBy === "day" ? 'dd MMM' : 'MMM yyyy' };
  }
}