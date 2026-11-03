import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RoomList } from './room-list/room-list';
import { RoomForm } from './room-form/room-form';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RoomList, RoomForm],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
