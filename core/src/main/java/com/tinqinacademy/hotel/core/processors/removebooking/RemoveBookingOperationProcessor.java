package com.tinqinacademy.hotel.core.processors.removebooking;

import com.tinqinacademy.hotel.api.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.operations.removebooking.input.RemoveBookingInput;
import com.tinqinacademy.hotel.api.operations.removebooking.operation.RemoveBookingOperation;
import com.tinqinacademy.hotel.api.operations.removebooking.output.RemoveBookingOutput;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.UUID;

import static io.vavr.API.Match;

@Service
@Slf4j
public class RemoveBookingOperationProcessor extends BaseOperationProcessor implements RemoveBookingOperation {

  private final BookingRepository bookingRepository;

  public RemoveBookingOperationProcessor(
      ConversionService conversionService,
      Validator validator, BookingRepository bookingRepository
  ) {
    super(conversionService, validator);
    this.bookingRepository = bookingRepository;
  }

  @Override
  public Either<ErrorOutput, RemoveBookingOutput> process(RemoveBookingInput input) {
    return validateInput(input)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start removeBooking input: {}", validInput);
                  UUID bookingId = UUID.fromString(validInput.getBookingId());

                  Booking booking = getBookingByIdOrThrow(bookingId);

                  bookingRepository.delete(booking);

                  RemoveBookingOutput output = createOutput();
                  log.info("End removeBooking output: {}", output);
                  return output;
                })
                .toEither()
                .mapLeft(t -> Match(t).of(
                    customStatusCase(t, EntityNotFoundException.class, HttpStatus.NOT_FOUND),
                    customStatusCase(t, IllegalArgumentException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                    defaultCase(t)
                ))
        );
  }

  private Booking getBookingByIdOrThrow(UUID bookingId) {
    return bookingRepository.findById(bookingId)
        .orElseThrow(() -> new EntityNotFoundException("Booking", "id", bookingId.toString()));
  }

  private RemoveBookingOutput createOutput() {
    return RemoveBookingOutput.builder()
        .build();
  }
}
