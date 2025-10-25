import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { GlobalService } from '../../shared/services/global-urls.service';
import { PurchaseRequestDto } from '../models/purchase/purchase-request.model';
import { PurchaseResponseDto } from '../models/purchase/purchase-response.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SupplyPurchaseService {

  private readonly http = inject(HttpClient);
  private readonly globalUrls = inject(GlobalService);
  private apiUrl = this.globalUrls.apiUrlLocalHost8082;

  saveSupplyPurchase(purchaseRequestDto: PurchaseRequestDto): Observable<PurchaseResponseDto> {
    return this.http.post<PurchaseResponseDto>(this.apiUrl + "/supplies-purchases", purchaseRequestDto);
  }
}
