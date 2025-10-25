import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WindowService {
  getPageSize(): number {
    const h = window.innerHeight;
    if (h >= 4320) return 24;
    if (h >= 2880) return 22;
    if (h >= 2160) return 18;
    if (h >= 1440) return 15;
    if (h >= 1100) return 11;
    if (h >= 920) return 8;
    if (h >= 880) return 7;
    if (h >= 798) return 6;
    if (h >= 720) return 5;
    return 3;
  }
}
