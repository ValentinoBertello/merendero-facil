export interface LotDto {
  id: number;
  initialQuantity: number;
  currentQuantity: number;
  expirationDate: string;
  daysToExpire: number;
}