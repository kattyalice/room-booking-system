package com.kattyalice.booking.booking;

import java.time.Instant;

public record BookingResponse(
        Long id,
        Long roomId,
        String roomName,
        String bookedBy,
        String title,
        Instant startTime,
        Instant endTime,
        BookingStatus status,
        Instant createdAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getRoom().getName(),
                booking.getBookedBy(),
                booking.getTitle(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
