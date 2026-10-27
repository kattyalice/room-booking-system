package com.kattyalice.booking.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record BookingRequest(
        @NotNull(message = "Room is required.")
        Long roomId,

        @NotBlank(message = "Booked by is required.")
        @Size(max = 100, message = "Booked by must be 100 characters or fewer.")
        String bookedBy,

        @NotBlank(message = "Title is required.")
        @Size(max = 200, message = "Title must be 200 characters or fewer.")
        String title,

        @NotNull(message = "Start time is required.")
        Instant startTime,

        @NotNull(message = "End time is required.")
        Instant endTime
) {
}
