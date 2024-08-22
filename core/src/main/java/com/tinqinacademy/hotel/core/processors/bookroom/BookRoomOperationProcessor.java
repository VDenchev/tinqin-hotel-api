package com.tinqinacademy.hotel.core.processors.bookroom;

import com.tinqinacademy.hotel.api.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.exceptions.RoomUnavailableException;
import com.tinqinacademy.hotel.api.operations.bookroom.input.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.operation.BookRoomOperation;
import com.tinqinacademy.hotel.api.operations.bookroom.output.BookRoomOutput;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.vavr.API.Match;

@Service
@Slf4j
public class BookRoomOperationProcessor extends BaseOperationProcessor implements BookRoomOperation {

  private final BookingRepository bookingRepository;
  private final RoomRepository roomRepository;

  @Autowired
  public BookRoomOperationProcessor(
      ConversionService conversionService, Validator validator,
      BookingRepository bookingRepository,
      RoomRepository roomRepository
  ) {
    super(conversionService, validator);
    this.bookingRepository = bookingRepository;
    this.roomRepository = roomRepository;
  }

  @Override
  @Transactional
  public Either<ErrorOutput, BookRoomOutput> process(BookRoomInput input) {
    return validateInput(input)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start bookRoom input: {}", validInput);

                  UUID roomId = UUID.fromString(validInput.getRoomId());

                  Room room = roomRepository.findById(roomId)
                      .orElseThrow(() -> new EntityNotFoundException("Room", "id", validInput.getRoomId()));

                  ensueRoomIsNotAlreadyBookedForTheSamePeriod(roomId, validInput.getStartDate(),
                      validInput.getEndDate());

                  Booking booking = convertBookRoomInputToBooking(validInput, room);

                  bookingRepository.save(booking);

                  BookRoomOutput output = createOutput();
                  log.info("End bookRoom output: {}", output);
                  return output;
                })
                .toEither()
                .mapLeft(t -> Match(t).of(
                    customStatusCase(t, EntityNotFoundException.class, HttpStatus.NOT_FOUND),
                    customStatusCase(t, RoomUnavailableException.class, HttpStatus.CONFLICT),
                    customStatusCase(t, IllegalArgumentException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                    defaultCase(t)
                ))
        );
  }

  private void ensueRoomIsNotAlreadyBookedForTheSamePeriod(UUID roomId, LocalDate startDate, LocalDate endDate) {
    List<Booking> roomBookingsForPeriod = bookingRepository.getBookingsOfRoomForPeriod(roomId, startDate, endDate);

    if (!roomBookingsForPeriod.isEmpty()) {
      throw new RoomUnavailableException("Room has already been booked for the specified period");
    }
  }

  private Booking convertBookRoomInputToBooking(
      BookRoomInput input,
      Room room
  ) {
    Booking booking = conversionService.convert(input, Booking.class);
    booking.setRoom(room);
    return booking;
  }

  private BookRoomOutput createOutput() {
    return BookRoomOutput.builder()
        .build();
  }
}
