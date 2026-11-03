export interface Room {
    id: number;
    name: string;
    capacity: number;
    location: string | null;
    createdAt: string;
}

export interface RoomRequest {
    name: string;
    capacity: number | null;
    location: string | null;
}