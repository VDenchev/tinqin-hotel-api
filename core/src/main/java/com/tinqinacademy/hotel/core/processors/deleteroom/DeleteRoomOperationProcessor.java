package com.tinqinacademy.hotel.core.processors.deleteroom;

import com.tinqinacademy.hotel.api.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.exceptions.RoomUnavailableException;
import com.tinqinacademy.hotel.api.operations.deleteroom.input.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.operation.DeleteRoomOperation;
import com.tinqinacademy.hotel.api.operations.deleteroom.output.DeleteRoomOutput;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
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
public class DeleteRoomOperationProcessor extends BaseOperationProcessor implements DeleteRoomOperation {

  private final RoomRepository roomRepository;
  private final BookingRepository bookingRepository;

  public DeleteRoomOperationProcessor(ConversionService conversionService, Validator validator, RoomRepository roomRepository, BookingRepository bookingRepository) {
    super(conversionService, validator);
    this.roomRepository = roomRepository;
    this.bookingRepository = bookingRepository;
  }

  @Override
  @Transactional
  public Either<ErrorOutput, DeleteRoomOutput> process(DeleteRoomInput input) {
    return validateInput(input)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start deleteRoom input: {}", validInput);
                  UUID roomId = UUID.fromString(input.getId());

                  Room room = getRoomByIdOrThrow(roomId);
                  List<Booking> roomBookings = bookingRepository.getBookingsOfRoomForPeriod(roomId, LocalDate.now(), LocalDate.now().plusYears(10_000));

                  if (!roomBookings.isEmpty()) {
                    throw new RoomUnavailableException("Unable to delete room: Room is still being used");
                  }

                  bookingRepository.deleteBookingsByRoom(room);
                  roomRepository.delete(room);

                  DeleteRoomOutput output = createOutput();
                  log.info("End deleteRoom output: {}", output);
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

  private DeleteRoomOutput createOutput() {
    return DeleteRoomOutput.builder()
        .build();
  }

  private Room getRoomByIdOrThrow(UUID roomId) {
    return roomRepository.findById(roomId)
        .orElseThrow(() -> new EntityNotFoundException("Room", "id", roomId.toString()));
  }
}
