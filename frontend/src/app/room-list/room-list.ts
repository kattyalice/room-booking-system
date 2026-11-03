import { RoomService } from '../services/room.service';
import { inject, signal, Component, OnInit } from '@angular/core';
import { Room } from '../models/room';

@Component({
  selector: 'app-room-list',
  templateUrl: './room-list.html',
  styleUrl: './room-list.css',
})
export class RoomList implements OnInit {
  private roomService = inject(RoomService);
  rooms = signal<Room[]>([]);

  ngOnInit() {
    this.roomService.listRooms().subscribe(data => this.rooms.set(data));
  }
}
