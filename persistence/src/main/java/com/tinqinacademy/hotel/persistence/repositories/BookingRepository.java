package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking,UUID> {

  @Query(value = """
      FROM bookings b
      WHERE b.room.id = :roomId
      AND(:startDate <= b.endDate AND :endDate >= b.startDate)
      """
  )
  List<Booking> getBookingsOfRoomForPeriod(UUID roomId, LocalDate startDate, LocalDate endDate);
}
