export interface ExpenseResponseDto {
  id: number;
  amount: number;
  merenderoId: number;
  type: string;
  expenseDate: string;

  supplyId: number;
  supplyName: string;
  quantity: number;
  unit: string;
}