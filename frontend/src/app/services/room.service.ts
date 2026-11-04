import { HttpClient } from '@angular/common/http';
import { Injectable, inject} from '@angular/core';
import { Room, RoomRequest } from '../models/room';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RoomService {
    private http = inject(HttpClient);

    private baseUrl = `${environment.apiUrl}/rooms`;

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