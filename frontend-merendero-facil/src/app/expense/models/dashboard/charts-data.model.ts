import { ExpenseBySupplyDto } from "./expense-by-supply.model";
import { ExpenseByTypeDto } from "./expense-by-type.model";
import { TimeGroupedExpenseData } from "./time-grouped-data.model";

export interface ChartsData {
    expensesByType: ExpenseByTypeDto[];
    expensesBySupply: ExpenseBySupplyDto[];
    timeGroupedData: TimeGroupedExpenseData[];
}