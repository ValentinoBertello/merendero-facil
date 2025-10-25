export interface DonationResponseDto {
  id: number;
  userId: number;
  userEmail: string;
  merenderoId: number;
  donationDate: string;
  paymentId: string;
  grossAmount: number;
  mpFee: number;
  netAmount: number;
}