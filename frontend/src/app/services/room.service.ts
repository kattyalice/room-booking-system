import { HttpClient } from '@angular/common/http';
import { Injectable, inject} from '@angular/core';
import { Room, RoomRequest } from '../models/room';

@Injectable({ providedIn: 'root' })
export class RoomService {
    private http = inject(HttpClient);

    private baseUrl = 'http://localhost:8080/api/rooms';

    listRooms() {
        return this.http.get<Room[]>(this.baseUrl);
    }

    createRoom(request: RoomRequest) {
        return this.http.post<Room>(this.baseUrl, request);
    }

    getRoom(id: number) {
        return this.http.get<Room>(`${this.baseUrl}/${id}`);
    }
}