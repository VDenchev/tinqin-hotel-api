package com.tinqinacademy.hotel.core.services;

import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.exceptions.RoomUnavailableException;
import com.tinqinacademy.hotel.api.models.output.DatesOccupied;
import com.tinqinacademy.hotel.api.models.output.RoomOutput;
import com.tinqinacademy.hotel.api.operations.bookroom.input.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.output.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.input.AvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.output.AvailableRoomsOutput;
import com.tinqinacademy.hotel.api.operations.getroom.input.RoomDetailsInput;
import com.tinqinacademy.hotel.api.operations.getroom.output.RoomDetailsOutput;
import com.tinqinacademy.hotel.api.operations.removebooking.input.RemoveBookingInput;
import com.tinqinacademy.hotel.api.operations.removebooking.output.RemoveBookingOutput;
import com.tinqinacademy.hotel.api.services.contracts.HotelService;
import com.tinqinacademy.hotel.core.mappers.BookingMapper;
import com.tinqinacademy.hotel.core.mappers.RoomMapper;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.entities.user.User;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.hotel.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

  private final RoomRepository roomRepository;
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;
  private final ConversionService conversionService;

  @Override
  @Transactional
  public BookRoomOutput bookRoom(BookRoomInput input) {
    log.info("Start bookRoom input: {}", input);

    Optional<User> userOpt = userRepository.findByPhoneNumber(input.getPhoneNumber());

    List<Booking> roomBookingsForPeriod = bookingRepository.getBookingsOfRoomForPeriod(input.getRoomId(), input.getStartDate(), input.getEndDate());

    if (!roomBookingsForPeriod.isEmpty()) {
      throw new RoomUnavailableException("Room has already been booked for the specified period");
    }

    User user;
    if (userOpt.isEmpty()) {
      user = User.builder()
          .firstName(input.getFirstName())
          .lastName(input.getLastName())
          .phoneNumber(input.getPhoneNumber())
          .email("no.email@example.com")
          .password("password")
          .build();
      userRepository.save(user);
    } else {
      user = userOpt.get();
    }

    Room room = roomRepository.findById(input.getRoomId())
        .orElseThrow(() -> new EntityNotFoundException("Room", input.getRoomId()));

    Booking booking = conversionService.convert(input, Booking.class);
    booking.setRoom(room);
    booking.setUser(user);

    log.info("Booking : {}", booking);

    bookingRepository.save(booking);

    BookRoomOutput output = BookRoomOutput.builder()
        .build();

    log.info("End bookRoom output: {}", output);
    return output;
  }

  @Override
  public RoomDetailsOutput getRoom(RoomDetailsInput input) {
    log.info("Start getRoom input: {}", input);

    Room room = roomRepository.findById(input.getId())
        .orElseThrow(() -> new EntityNotFoundException("Room", input.getId()));

    List<LocalDate> dates = room.getBookings().stream()
        .flatMap(b -> b.getStartDate().datesUntil(b.getEndDate().plusDays(1)))
        .toList();
    DatesOccupied datesOccupied = DatesOccupied.builder()
        .dates(dates)
        .build();

    List<Bed> beds = roomRepository.getAllBedsByRoomId(room.getId());
    RoomOutput output = conversionService.convert(room, RoomOutput.class);
    output.setBedSizes(beds.stream().map(b -> conversionService.convert(b, BedType.class)).toList());
    output.setBathroomType(conversionService.convert(room.getBathroomType(), com.tinqinacademy.hotel.api.enums.BathroomType.class));
    output.setBedCount(beds.size());
    output.setDatesOccupied(datesOccupied);

    log.info("End getRoom output: {}", output);
    return RoomDetailsOutput.builder()
        .roomOutput(output)
        .build();
  }

  @Override
  public AvailableRoomsOutput checkAvailableRooms(AvailableRoomsInput input) {
    log.info("Start checkAvailableRooms input: {}", input);

    LocalDate startDate = LocalDate.now();
    if (input.getStartDate() != null) {
      startDate = input.getStartDate();
    }

    LocalDate endDate = startDate.plusDays(7);
    if (input.getEndDate() != null) {
      endDate = input.getEndDate();
    }

    if (input.getBedCount() == null) {
      input.setBedCount(0);
    }

    List<String> bedSizes = input.getBedTypes().stream().map(b -> BedSize.getByCode(b.getCode()).name()).toList();
    BathroomType bathroomType = conversionService.convert(input.getBathroomType(), BathroomType.class);
    List<UUID> result = roomRepository.findAllAvailableRoomIds(startDate, endDate, bathroomType.name(), bedSizes, input.getBedCount());

    log.info("End checkAvailableRooms output: {}", result);
    return AvailableRoomsOutput.builder()
        .roomIds(result)
        .build();
  }

  @Override
  public RemoveBookingOutput removeBooking(RemoveBookingInput input) {
    log.info("Start removeBooking input: {}", input);

    Booking booking = bookingRepository.findById(input.getBookingId())
        .orElseThrow(() -> new EntityNotFoundException("Booking", input.getBookingId()));

    bookingRepository.delete(booking);

    RemoveBookingOutput output = RemoveBookingOutput.builder()
        .build();

    log.info("End removeBooking output: {}", output);
    return output;
  }
}
