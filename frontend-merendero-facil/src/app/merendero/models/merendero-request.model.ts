export interface MerenderoRequestDto {
  name: string;
  address: string;
  latitude: number;
  longitude: number;

  capacity: number;
  daysOpen: string;

  openingTime: string;
  closingTime: string;
  managerEmail: string;
}