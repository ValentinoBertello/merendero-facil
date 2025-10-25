import { EntryRequestDto } from "../movement/entry-request.model";
import { ExpenseRequestDto } from "../../../expense/models/expense-request.model";

export interface PurchaseRequestDto {
    entryRequestDto: EntryRequestDto;
    expenseRequestDto: ExpenseRequestDto;
}