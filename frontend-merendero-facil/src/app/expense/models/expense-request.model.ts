export interface ExpenseRequestDto {
  merenderoId: number;
  amount: number;
  typeExpenseId: number;

  entryId?: number;
}