export interface MerenderoResponseDto {
  id: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;

  capacity: number;
  daysOpen: string;

  openingTime: string;
  closingTime: string;
  managerId: number;
  managerEmail: string;
  active: boolean;
  openNow: boolean;
}