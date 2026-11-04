import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Booking, BookingRequest } from '../models/booking';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private http = inject(HttpClient);

  private baseUrl = 'http://localhost:8080/api';

  listBookingsForRoom(roomId: number) {
    return this.http.get<Booking[]>(`${this.baseUrl}/rooms/${roomId}/bookings`);
  }

  createBooking(request: BookingRequest) {
    return this.http.post<Booking>(`${this.baseUrl}/bookings`, request);
  }
}
