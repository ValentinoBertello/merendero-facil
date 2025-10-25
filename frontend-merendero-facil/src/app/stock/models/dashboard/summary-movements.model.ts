import { GroupMovementsDto } from "./group-movements.model";

export class SummaryMovementsDto {
  // Promedios
  avgEntryDay: number;
  avgEntryWeek: number;
  avgOutputDay: number;
  avgOutputWeek: number;

  // Totales
  totalEntry: number;
  totalOutput: number;

  // Tipos de entrada
  entryDonationQty: number;
  entryPurchaseQty: number;

  // Porcentajes
  percentageVariationEntry: number;
  percentageVariationOutput: number;

  // Grupos de movimientos
  groupsMovements: GroupMovementsDto[];

  constructor() {
    this.avgEntryDay = 0;
    this.avgEntryWeek = 0;
    this.avgOutputDay = 0;
    this.avgOutputWeek = 0;

    this.totalEntry = 0;
    this.totalOutput = 0;

    this.entryDonationQty = 0;
    this.entryPurchaseQty = 0;

    this.percentageVariationEntry = 0;
    this.percentageVariationOutput = 0;

    this.groupsMovements = [];
  }

  // Método estático helper para inicializar
  static empty(): SummaryMovementsDto {
    return new SummaryMovementsDto();
  }
}