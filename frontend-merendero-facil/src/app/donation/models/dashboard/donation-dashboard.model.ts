import { ComparisonStats } from "./comparison-stats.model";
import { DonationDateSummary } from "./donation-date-summary.model";
import { DonorAnalysis } from "./donor-analysis.model";
import { PeriodStats } from "./period-stats.model";

export interface DonationDashboardResponse {
    currentPeriod: PeriodStats;
    previousPeriod: PeriodStats;
    comparisonStats: ComparisonStats;
    donationDateSummaries: DonationDateSummary[];
    donorAnalysis: DonorAnalysis;
}