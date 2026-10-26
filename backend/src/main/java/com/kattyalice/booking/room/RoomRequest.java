package com.kattyalice.booking.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RoomRequest(
        @NotBlank(message = "Name is required.")
        @Size(max = 100, message = "Name must be 100 characters or fewer.")
        String name,

        @NotNull(message = "Capacity is required.")
        @Positive(message = "Capacity must be at least 1.")
        Integer capacity,

        @Size(max = 100, message = "Location must be 100 characters or fewer.")
        String location
    ) {
}
