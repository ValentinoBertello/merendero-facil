import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  get apiUrlLocalHost8080(): string {
    return "http://localhost:8080";
  }

  get apiUrlLocalHost8081(): string {
    return "http://localhost:8081";
  }

  get apiUrlLocalHost8082(): string {
    return "http://localhost:8082"
  }
 }