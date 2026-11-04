export interface Booking {
    id: number;
    roomId: number;
    roomName: string;
    bookedBy: string;
    title: string;
    startTime: string;
    endTime: string;
    status: 'CONFIRMED' | 'CANCELLED';
    createdAt: string;
}

export interface BookingRequest {
    roomId: number;
    bookedBy: string;
    title: string;
    startTime: string | null;
    endTime: string | null;
}