package com.kattyalice.booking.room;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}
