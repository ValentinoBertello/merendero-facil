import { DonorTypeAnalysis } from "./donor-type-analysis.model";
import { TopDonor } from "./top-donor.model";

export interface DonorAnalysis {
  topDonors: TopDonor[];
  donorTypeAnalysis: DonorTypeAnalysis;
}