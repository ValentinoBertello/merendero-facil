export interface EntryResponseDto {
  id: number;
  supplyId: number;
  supplyName: string;
  category: string;
  unit: string;
  quantity: number;
  entryDate: string
  entryType: string;
  cost?: number;
}