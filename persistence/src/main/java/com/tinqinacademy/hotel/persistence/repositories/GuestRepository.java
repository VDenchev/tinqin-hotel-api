package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entities.guest.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {
  Optional<Guest> findGuestByIdCardNumber(String idCardNumber);

  @Query("""
           SELECT g
           FROM bookings b JOIN b.guests g
           WHERE g.idCardNumber IN :idCardNumbers
           AND b.id = :bookingId
      """)
  List<Guest> getAllGuestsByBookingIdAndIdCardNumberList(UUID bookingId, List<String> idCardNumbers);

  @Query("""
           SELECT g
           FROM bookings b JOIN b.guests g
           WHERE g.idCardNumber IN :idCardNumbers
      """)
  List<Guest> getAllGuestsByIdCardNumberList(List<String> idCardNumbers);
}
