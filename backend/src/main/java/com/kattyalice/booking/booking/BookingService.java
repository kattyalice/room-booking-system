package com.kattyalice.booking.booking;

import com.kattyalice.booking.common.ConflictException;
import com.kattyalice.booking.common.NotFoundException;
import com.kattyalice.booking.room.Room;
import com.kattyalice.booking.room.RoomService;

import java.awt.print.Book;
import java.time.Instant;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    public BookingService(BookingRepository bookingRepository, RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
    }

    @Transactional
    public BookingResponse createBooking(Long roomId, String bookedBy, String title, Instant startTime, Instant endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        Room room = roomService.getRoom(roomId);
        Booking booking = new Booking(room, bookedBy, title, startTime, endTime);

        try {
            Booking saved = bookingRepository.saveAndFlush(booking);
            return BookingResponse.from(saved);
        } catch (DataIntegrityViolationException ex) {
            if (isOverlap(ex)) {
                throw new ConflictException(room.getName() + " is already booked for that time.");
            }
            throw ex;
        }
    }

    private boolean isOverlap(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        return message != null && message.contains("no_overlapping_bookings");
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() ->new NotFoundException("Booking " + id + " not found."));
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id) {
        return BookingResponse.from(findBooking(id));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listBookingsForRoom(Long roomId) {
        roomService.getRoom(roomId);

        return bookingRepository.findByRoomIdOrderByStartTimeAsc(roomId).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = findBooking(id);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException("Booking " + id + " is already cancelled.");
        }

        booking.cancel();

        return BookingResponse.from(booking);
    }
}
