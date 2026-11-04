import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Booking, BookingRequest } from '../models/booking';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private http = inject(HttpClient);

  private baseUrl = environment.apiUrl;

  listBookingsForRoom(roomId: number) {
    return this.http.get<Booking[]>(`${this.baseUrl}/rooms/${roomId}/bookings`);
  }

  createBooking(request: BookingRequest) {
    return this.http.post<Booking>(`${this.baseUrl}/bookings`, request);
  }

  cancelBooking(bookingId: number) {
    return this.http.post<Booking>(`${this.baseUrl}/bookings/${bookingId}/cancel`, {});
  }
}
