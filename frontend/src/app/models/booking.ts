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