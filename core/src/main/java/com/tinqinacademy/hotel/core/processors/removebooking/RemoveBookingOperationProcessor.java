package com.tinqinacademy.hotel.core.processors.removebooking;

import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.operations.removebooking.input.RemoveBookingInput;
import com.tinqinacademy.hotel.api.operations.removebooking.operation.RemoveBookingOperation;
import com.tinqinacademy.hotel.api.operations.removebooking.output.RemoveBookingOutput;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RemoveBookingOperationProcessor implements RemoveBookingOperation {

  private final BookingRepository bookingRepository;

  @Override
  public RemoveBookingOutput process(RemoveBookingInput input) {
    log.info("Start removeBooking input: {}", input);

    Booking booking = getBookingByIdOrThrow(input.getBookingId());

    bookingRepository.delete(booking);

    RemoveBookingOutput output = createOutput();
    log.info("End removeBooking output: {}", output);
    return output;
  }

  private Booking getBookingByIdOrThrow(UUID bookingId) {
    return bookingRepository.findById(bookingId)
        .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));
  }

  private RemoveBookingOutput createOutput() {
    return RemoveBookingOutput.builder()
        .build();
  }
}
