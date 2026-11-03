import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RoomService } from '../services/room.service';


@Component({
  selector: 'app-room-form',
  imports: [ReactiveFormsModule],
  templateUrl: './room-form.html',
  styleUrl: './room-form.css',
})
export class RoomForm{ 
  form = new FormGroup({
    name: new FormControl('', { nonNullable: true}),
    capacity: new FormControl<number | null>(null),
    location: new FormControl('', { nonNullable: false})
  });

  private roomService = inject(RoomService);
  errors = signal<Record<string, string>>({});

  save() {
    const request = this.form.getRawValue();

    this.roomService.createRoom(request).subscribe({
      next: (room) => {
        console.log('Saved', room);
        this.errors.set({});
        this.form.reset();
      },
      error: (err) => {
        console.log('Failed', err);
        this.errors.set(err.error?.errors ?? {});
      }
    });
  }
}