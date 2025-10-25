import { ChartsData } from "./charts-data.model";
import { ExpenseSummaryData } from "./expense-summary-data.model";

export interface ExpenseDashboardResponse {
    expenseSummaryData: ExpenseSummaryData;
    chartsData: ChartsData;
}