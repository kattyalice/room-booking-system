import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { RoomService } from '../services/room.service';
import { Room } from '../models/room';
import { BookingService } from '../services/booking.service';
import { Booking } from '../models/booking';
import { DatePipe } from '@angular/common';
import { BookingForm } from '../booking-form/booking-form';

@Component({
  selector: 'app-room-detail',
  imports: [RouterLink, DatePipe, BookingForm],
  templateUrl: './room-detail.html',
  styleUrl: './room-detail.css',
})
export class RoomDetail implements OnInit{
  private route = inject(ActivatedRoute);
  private roomService = inject(RoomService);
  private bookingService = inject(BookingService);

  room = signal<Room | null>(null);
  bookings = signal<Booking[]>([]);

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.roomService.getRoom(id).subscribe(data => this.room.set(data));
    this.bookingService.listBookingsForRoom(id).subscribe(data => this.bookings.set(data));
  }

  addBooking(booking: Booking) {
    this.bookings.update(current => 
      [...current, booking].sort((a, b) => a.startTime.localeCompare(b.startTime))
    );
  }

  cancelBooking(booking: Booking) {
    this.bookingService.cancelBooking(booking.id).subscribe({
      next: (updated) => {
        this.bookings.update(current =>
          current.map(b => b.id === updated.id ? updated : b)
        );
      },
      error: (err) => {
        console.log('Cancel failed.', err);
      },
    });
  }
}
