export interface ItemStockDto {
  supplyId: number;
  supplyName: string;
  minQuantity: number;
  unit: string;
  category: string;
  totalStock: number;
  nextExpiration: string;
}