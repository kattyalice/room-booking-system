import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RoomList } from './room-list/room-list';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RoomList],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
