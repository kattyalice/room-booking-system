package com.kattyalice.booking.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRoomIdOrderByStartTimeAsc(Long roomId);
}
