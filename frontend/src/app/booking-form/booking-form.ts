import { Component, input, signal, inject, output } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import { BookingRequest, Booking } from '../models/booking';
import { BookingService } from '../services/booking.service';

@Component({
  selector: 'app-booking-form',
  imports: [ReactiveFormsModule],
  templateUrl: './booking-form.html',
  styleUrl: './booking-form.css',
})
export class BookingForm {
  private bookingService = inject(BookingService);
  
  roomId = input.required<number>();
  bookingCreated = output<Booking>();
  errors = signal<Record<string, string>>({});
  formError = signal<string | null>(null);

  form = new FormGroup({
    bookedBy: new FormControl('', { nonNullable: true }),
    title: new FormControl('', { nonNullable: true }),
    startTime: new FormControl('', { nonNullable: true }),
    endTime: new FormControl('', { nonNullable: true })
  });

  save() {
    const value = this.form.getRawValue();

    const request: BookingRequest = {
      roomId: this.roomId(),
      bookedBy: value.bookedBy,
      title: value.title,
      startTime: this.toUtc(value.startTime),
      endTime: this.toUtc(value.endTime)
    };

    this.bookingService.createBooking(request).subscribe({
      next: (booking) => {
        console.log('Saved', booking);
        this.errors.set({});
        this.formError.set(null);
        this.form.reset();
        this.bookingCreated.emit(booking);
      },
      error: (err) => {
        console.log('Failed', err);
        this.errors.set(err.error?.errors ?? {});
      
        if (err.error?.errors) {
          this.formError.set(null);
        } else {
          this.formError.set(err.error?.detail ?? 'Something went wrong. Please try again.');
        }
      }
    });
  }

  private toUtc(value: string): string | null {
    return value ? new Date(value).toISOString() : null;
  }
}
