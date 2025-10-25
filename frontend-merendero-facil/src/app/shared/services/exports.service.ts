import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { DatesService } from './dates.service';

@Injectable({
  providedIn: 'root'
})
export class ExportsService {

  constructor(private datesService: DatesService) { }

  /* Exporta un array de objetos "planos" a Excel. */
  public exportExcel(
    data: any[],
    fileNamePrefix: string,
    sheetName: string = 'Datos'
  ): void {
    const formattedDate = new Date().toLocaleDateString('en-GB').replace(/\//g, '-');
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(data);
    const colCount = data.length > 0 ? Object.keys(data[0]).length : 0;
    ws['!cols'] = Array(colCount).fill({ wch: 20 });
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, sheetName);
    const fileName = `${fileNamePrefix}_${formattedDate}.xlsx`;
    XLSX.writeFile(wb, fileName);
  }

  public exportPdf
    (
      bodyData: any[][],         // Array de arrays: cada fila de la tabla
      headers: string[],          // Cabeceras de la tabla
      fileNamePrefix: string,     // Nombre de archivo, ej: "donaciones"
      title: string,              // Título del reporte
      startDate?: string,         // Fecha inicio filtro (opcional)
      endDate?: string            // Fecha fin filtro (opcional)
    ) {
    const currentDate = new Date();
    const formattedDate = currentDate.toLocaleDateString('en-GB').replace(/\//g, '-');
    const doc = new jsPDF();

    // --- Título principal ---
    doc.setFontSize(16);
    doc.setTextColor(50, 50, 50);
    doc.text(title, 14, 20);

    // --- Fechas del reporte ---
    doc.setFontSize(10);
    doc.setTextColor(100);
    const startText = startDate
      ? this.datesService.formatStringToLocalDate(startDate).toLocaleDateString('es-AR', { day: 'numeric', month: 'numeric', year: 'numeric' })
      : 'No especificada';

    const endText = endDate
      ? this.datesService.formatStringToLocalDate(endDate).toLocaleDateString('es-AR', { day: 'numeric', month: 'numeric', year: 'numeric' })
      : 'No especificada';

    doc.text(`Desde: ${startText} hasta: ${endText}`, 14, 27);

    // --- Fecha de generación ---
    doc.text(`Generado: ${currentDate.toLocaleDateString('es-AR')}`, 14, 33);

    // --- Generar tabla ---
    autoTable(doc, {
      startY: 40,
      head: [headers],
      body: bodyData,
      headStyles: {
        fillColor: [90, 90, 90], // gris por defecto
        textColor: [255, 255, 255],
        fontStyle: 'bold'

      },
      styles: {
        cellPadding: 3,
        fontSize: 8,
        textColor: [50, 50, 50],
        overflow: 'linebreak',
        halign: 'left'
      },
      theme: 'grid',
      tableWidth: 'auto'
    });

    // --- Pie de página con numeración ---
    this.addPageNumbers(doc);

    // --- Guardar PDF ---
    doc.save(`${fileNamePrefix}_${formattedDate}.pdf`);
  }

  private addPageNumbers(doc: jsPDF) {
    const pageCount = doc.getNumberOfPages();
    for (let i = 1; i <= pageCount; i++) {
      doc.setPage(i);
      doc.setFontSize(8);
      doc.setTextColor(100);
      doc.text(
        `Página ${i} de ${pageCount}`,
        doc.internal.pageSize.getWidth() - 40,
        doc.internal.pageSize.getHeight() - 10
      );
    }
  }
}
