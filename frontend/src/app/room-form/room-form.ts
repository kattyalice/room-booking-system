import { Component, inject, signal, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RoomService } from '../services/room.service';
import { Room } from '../models/room';


@Component({
  selector: 'app-room-form',
  imports: [ReactiveFormsModule],
  templateUrl: './room-form.html',
  styleUrl: './room-form.css',
})
export class RoomForm {
  private roomService = inject(RoomService);
  roomCreated = output<Room>();
  errors = signal<Record<string, string>>({});
  formError = signal<string | null>(null);

  form = new FormGroup({
    name: new FormControl('', { nonNullable: true }),
    capacity: new FormControl<number | null>(null),
    location: new FormControl('', { nonNullable: false })
  });

  save() {
    const request = this.form.getRawValue();

    this.roomService.createRoom(request).subscribe({
      next: (room) => {
        console.log('Saved', room);
        this.errors.set({});
        this.formError.set(null);
        this.form.reset();
        this.roomCreated.emit(room);
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
}