package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import jakarta.persistence.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room,UUID> {

  public static final String ADD_BED_QUERY = "INSERT INTO bed_rooms (room_id, bed_id) VALUES(?, ?);";
  public static final String REMOVE_BED_QUERY = "DELETE FROM bed_rooms WHERE room_id = ?, bed_id = ?";
  public static final String REMOVE_ALL_BEDS = "DELETE FROM bed_rooms WHERE room_id = ?";

  Optional<Room> findRoomByNumber(String number);

  @Query(value = "select r.beds from rooms r where r.id = :roomId")
  List<Bed> getAllBedsByRoomId(UUID roomId);

  @Query(nativeQuery = true, value = """
        WITH BedCounts AS (
            SELECT r.id as room_id,
                COUNT(b.id) AS bed_count
            FROM rooms r
            JOIN room_beds rb ON r.id = rb.room_id
            JOIN beds b ON rb.bed_id = b.id
            GROUP BY r.id
        )
        SELECT r.id
        FROM rooms r
        JOIN room_beds rb ON rb.room_id = r.id
        JOIN beds b ON rb.bed_id = b.id
        JOIN BedCounts bc ON bc.room_id = r.id
        WHERE NOT EXISTS(
            SELECT 1
            FROM bookings bk
            WHERE bk.room_id = r.id
            AND (:startDate <= bk.end_date AND :endDate >= bk.start_date)
        )
        AND (:bedSizes IS NULL OR b.bed_size IN (:bedSizes))
        AND bc.bed_count::int >= :bedCount
        AND (:bathroomType IS NULL OR r.bathroom_type = :bathroomType)
        GROUP BY r.id;
      """)
  List<UUID> findAllAvailableRoomIds(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("bathroomType") String bathroomType,
      @Param("bedSizes") List<String> bedSizes,
      @Param("bedCount") Integer bedCount
      );
}
