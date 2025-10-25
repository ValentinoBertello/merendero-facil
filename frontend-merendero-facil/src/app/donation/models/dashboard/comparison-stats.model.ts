import { ChangeStats } from "./change-stats.model";

export interface ComparisonStats {
  amountDonatedChange: ChangeStats;
  donationCountChange: ChangeStats;
}