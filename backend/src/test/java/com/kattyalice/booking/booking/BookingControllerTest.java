package com.kattyalice.booking.booking;

import com.kattyalice.booking.common.ConflictException;
import com.kattyalice.booking.common.NotFoundException;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    private final Instant start = Instant.parse("2026-11-03T14:00:00Z");
    private final Instant end = Instant.parse("2026-11-03T15:00:00Z");

    private final String validJson = """
            {
              "roomId": 2,
              "bookedBy": "Kathryn",
              "title": "Standup",
              "startTime": "2026-11-03T14:00:00Z",
              "endTime": "2026-11-03T15:00:00Z"
            }
            """;

    @Test
    void createBooking_withValidBody_returns201() throws Exception {
        BookingResponse response = new BookingResponse(
                1L, 2L, "Room B", "Kathryn", "Standup", start, end, BookingStatus.CONFIRMED, start);
        when(bookingService.createBooking(any(), any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomName").value("Room B"));
    }

    @Test
    void createBooking_withoutTitle_returns400() throws Exception {
        String json = """
                {
                  "roomId": 2,
                  "bookedBy": "Kathryn",
                  "startTime": "2026-11-03T14:00:00Z",
                  "endTime": "2026-11-03T15:00:00Z"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required."));
    }

    @Test
    void createBooking_whenOverlapping_returns409() throws Exception {
        when(bookingService.createBooking(any(), any(), any(), any(), any()))
                .thenThrow(new ConflictException("Room B is already booked for that time."));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Room B is already booked for that time."));
    }

    @Test
    void getBooking_whenMissing_returns404() throws Exception {
        when(bookingService.getBooking(99L))
                .thenThrow(new NotFoundException("Booking 99 not found."));

        mockMvc.perform(get("/api/bookings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Booking 99 not found."));
    }

    @Test
    void cancelBooking_whenConfirmed_returns200() throws Exception {
        BookingResponse response = new BookingResponse(1L, 2L, "Room B", "Kathryn", "Standup", start, end, BookingStatus.CANCELLED, start);
        when(bookingService.cancelBooking(1L)).thenReturn(response);

        mockMvc.perform(post("/api/bookings/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
