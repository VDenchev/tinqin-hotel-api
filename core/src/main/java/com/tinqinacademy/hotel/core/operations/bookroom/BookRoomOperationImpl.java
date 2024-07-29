package com.tinqinacademy.hotel.core.operations.bookroom;

import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.exceptions.RoomUnavailableException;
import com.tinqinacademy.hotel.api.operations.bookroom.input.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.operation.BookRoomOperation;
import com.tinqinacademy.hotel.api.operations.bookroom.output.BookRoomOutput;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.entities.user.User;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.hotel.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookRoomOperationImpl implements BookRoomOperation {

  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;
  private final RoomRepository roomRepository;
  private final ConversionService conversionService;

  @Override
  public BookRoomOutput process(BookRoomInput input) {
    log.info("Start bookRoom input: {}", input);

    ensueRoomIsNotAlreadyBookedForTheSamePeriod(input.getRoomId(), input.getStartDate(),
        input.getEndDate());

    User user = getExistingOrCreateNewUser(input);
    Room room = roomRepository.findById(input.getRoomId())
        .orElseThrow(() -> new EntityNotFoundException("Room", input.getRoomId()));
    Booking booking = convertBookRoomInputToBooking(input, room, user);

    bookingRepository.save(booking);

    BookRoomOutput output = createOutput();
    log.info("End bookRoom output: {}", output);
    return output;
  }

  private void ensueRoomIsNotAlreadyBookedForTheSamePeriod(UUID roomId, LocalDate startDate, LocalDate endDate) {
    List<Booking> roomBookingsForPeriod = bookingRepository.getBookingsOfRoomForPeriod(roomId, startDate, endDate);

    if (!roomBookingsForPeriod.isEmpty()) {
      throw new RoomUnavailableException("Room has already been booked for the specified period");
    }
  }

  private User getExistingOrCreateNewUser(BookRoomInput input) {
    Optional<User> userMaybe = userRepository.findByPhoneNumber(input.getPhoneNumber());

    if (userMaybe.isEmpty()) {
      User user = User.builder()
          .firstName(input.getFirstName())
          .lastName(input.getLastName())
          .phoneNumber(input.getPhoneNumber())
          .email("no.email@example.com")
          .password("password")
          .build();
      return userRepository.save(user);
    }
    return userMaybe.get();
  }

  private Booking convertBookRoomInputToBooking(
      BookRoomInput input,
      Room room,
      User user
  ) {
    Booking booking = conversionService.convert(input, Booking.class);
    booking.setRoom(room);
    booking.setUser(user);
    return booking;
  }

  private BookRoomOutput createOutput() {
    return BookRoomOutput.builder()
        .build();
  }
}
