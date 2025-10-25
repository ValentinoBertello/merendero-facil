export interface SupplyRequestDto {
  name: string;
  unit: string;
  minQuantity: number;
  lastAlertDate: string;
  supplyCategoryId: number;
}