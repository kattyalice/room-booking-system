package com.kattyalice.booking.booking;

import jakarta.persistence.*;
import java.time.Instant;
import com.kattyalice.booking.room.Room;

@Entity
@Table(name = "bookings")

public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "booked_by", nullable = false, length = 100)
    private String bookedBy;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Booking(){
    }

    public Booking(Room room, String bookedBy, String title, Instant startTime, Instant endTime) {
        this.room = room;
        this.bookedBy = bookedBy;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @PrePersist
    void onCreate(){
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public String getTitle() {
        return title;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

}
