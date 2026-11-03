import { Routes } from '@angular/router';
import { RoomDetail } from './room-detail/room-detail';
import { RoomList } from './room-list/room-list';

export const routes: Routes = [
    { path: 'rooms/:id', component: RoomDetail },
    { path: '', component: RoomList},
];
