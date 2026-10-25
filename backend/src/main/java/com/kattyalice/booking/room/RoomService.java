package com.kattyalice.booking.room;

import com.kattyalice.booking.common.ConflictException;
import com.kattyalice.booking.common.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<Room> listRooms() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Room getRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room " + id + " not found."));
    }

    @Transactional
    public Room createRoom(String name, Integer capacity, String location) {
        validateCapacity(capacity);

        if (roomRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A room named '" + name + "' already exists.");
        }

        Room room = new Room(name, capacity, location);
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Long id, String name, Integer capacity, String location) {
        Room existingRoom = getRoom(id);

        validateCapacity(capacity);

        if (!existingRoom.getName().equalsIgnoreCase(name)
                && roomRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A room named '" + name + "' already exists.");
        }

        existingRoom.setName(name);
        existingRoom.setCapacity(capacity);
        existingRoom.setLocation(location);

        return roomRepository.save(existingRoom);
    }

    private void validateCapacity(Integer capacity) {
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
    }
}