import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Booking } from '../models/booking';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private http = inject(HttpClient);

  private baseUrl = 'http://localhost:8080/api';

  listBookingsForRoom(roomId: number) {
    return this.http.get<Booking[]>(`${this.baseUrl}/rooms/${roomId}/bookings`);
  }
}
