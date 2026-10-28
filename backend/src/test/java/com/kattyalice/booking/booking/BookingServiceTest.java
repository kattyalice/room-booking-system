package com.kattyalice.booking.booking;

import com.kattyalice.booking.common.ConflictException;
import com.kattyalice.booking.common.NotFoundException;
import com.kattyalice.booking.room.RoomService;
import com.kattyalice.booking.room.Room;
import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import javax.swing.text.html.Option;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private BookingService bookingService;

    private final Instant nineAm = Instant.parse("2024-11-03T14:00:00Z");
    private final Instant tenAm = Instant.parse("2026-11-03T15:00:00Z");

    @Test
    void createBooking_whenEndBeforeStart_throwsBadRequest() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(2L, "Kathryn", "Standup", tenAm, nineAm));
        verify(bookingRepository, never()).saveAndFlush(any(Booking.class));
    }

    @Test
    void getBooking_whenMissing_throwsNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(99L));
    }

    @Test
    void cancelBooking_whenConfirmed_setsStatusCancelled() {
        Room room = new Room("Room B", 6, "Floor 2");
        Booking booking = new Booking(room, "Kathryn", "Standup", nineAm, tenAm);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.cancelBooking(1L);

        assertEquals(BookingStatus.CANCELLED, response.status());
    }

    @Test
    void cancelBooking_whenAlreadyCancelled_throwsConflict() {
        Room room = new Room("Room B", 6, "Floor 2");
        Booking booking = new Booking(room, "Kathryn", "Standup", nineAm, tenAm);
        booking.cancel();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ConflictException.class,
                () -> bookingService.cancelBooking(1L));
    }

    @Test
    void createBooking_whenOverlapping_throwsConflict() {
        Room room = new Room("Room B", 6, "Floor 2");
        when(roomService.getRoom(2L)).thenReturn(room);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenThrow(new DataIntegrityViolationException("violates exclusion constraint no_overlapping_bookings"));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> bookingService.createBooking(2L, "Kathryn", "Standup", nineAm, tenAm));
        assertEquals("Room B is already booked for that time.", ex.getMessage());
    }
}
