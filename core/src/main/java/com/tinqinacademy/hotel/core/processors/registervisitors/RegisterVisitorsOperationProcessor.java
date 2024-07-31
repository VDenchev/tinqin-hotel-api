package com.tinqinacademy.hotel.core.processors.registervisitors;

import com.tinqinacademy.hotel.api.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.EntityAlreadyExistsException;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.exceptions.VisitorDateMismatchException;
import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.input.RegisterVisitorsInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.operation.RegisterVisitorsOperation;
import com.tinqinacademy.hotel.api.operations.registervisitors.output.RegisterVisitorsOutput;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.guest.Guest;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.vavr.API.Match;

@Service
@Slf4j
public class RegisterVisitorsOperationProcessor extends BaseOperationProcessor implements RegisterVisitorsOperation {

  private final BookingRepository bookingRepository;
  private final GuestRepository guestRepository;

  public RegisterVisitorsOperationProcessor(
      ConversionService conversionService, Validator validator,
      BookingRepository bookingRepository, GuestRepository guestRepository
  ) {
    super(conversionService, validator);
    this.bookingRepository = bookingRepository;
    this.guestRepository = guestRepository;
  }

  @Override
  public Either<ErrorOutput, RegisterVisitorsOutput> process(RegisterVisitorsInput input) {
    return Try.of(() -> {

          log.info("Start registerVisitors input: {}", input);

          Booking booking = bookingRepository.findById(input.getBookingId())
              .orElseThrow(() -> new EntityNotFoundException("Booking", input.getBookingId()));

          validateVisitors(input.getVisitors(), booking);

          List<String> inputIdCardNumbers = input.getVisitors().stream()
              .map(VisitorDetailsInput::getIdCardNo)
              .toList();
          //TODO: check for duplicate guests in input
          checkIfGuestsAlreadyInBooking(booking, inputIdCardNumbers);

          List<Guest> existingGuests = guestRepository.getAllGuestsByIdCardNumberList(inputIdCardNumbers);
          List<Guest> guestsToSave = getNotYetPersistedVisitors(existingGuests,
              input.getVisitors());

          log.info("Guests to be saved: {}", guestsToSave);

          guestsToSave = guestRepository.saveAll(guestsToSave);
          booking.getGuests().addAll(guestsToSave);
          booking.getGuests().addAll(existingGuests);
          bookingRepository.save(booking);

          RegisterVisitorsOutput output = createOutput();

          log.info("End registerVisitors output: {}", output);
          return output;
        })
        .toEither()
        .mapLeft(t -> Match(t).of(
            customStatusCase(t, VisitorDateMismatchException.class, HttpStatus.UNPROCESSABLE_ENTITY),
            customStatusCase(t, EntityNotFoundException.class, HttpStatus.BAD_REQUEST),
            defaultCase(t)
        ));
  }

  private void validateVisitors(List<VisitorDetailsInput> visitors, Booking booking) {
    boolean validVisitors = visitors.stream()
        .allMatch(v -> checkVisitorValidity(v, booking));

    if (!validVisitors) {
      throw new VisitorDateMismatchException("Visitor start and end dates must match the booking dates");
    }
  }

  private Boolean checkVisitorValidity(VisitorDetailsInput visitor, Booking booking) {
    return (visitor.getStartDate().isBefore(visitor.getEndDate()) || visitor.getStartDate().isEqual(visitor.getEndDate()))
        && (visitor.getStartDate().isAfter(booking.getStartDate()) || visitor.getStartDate().isEqual(booking.getStartDate()))
        && (visitor.getEndDate().isBefore(booking.getEndDate()) || visitor.getEndDate().isEqual(booking.getEndDate()));
  }

  private List<Guest> getNotYetPersistedVisitors(List<Guest> existingGuests, List<VisitorDetailsInput> visitors) {
    Set<String> existingGuestsIdCardNumbers = existingGuests.stream()
        .map(Guest::getIdCardNumber)
        .collect(Collectors.toSet());

    List<VisitorDetailsInput> filteredInput = visitors.stream()
        .filter(v -> !existingGuestsIdCardNumbers.contains(v.getIdCardNo()))
        .toList();
    return filteredInput.stream()
        .map(fi -> conversionService.convert(fi, Guest.class))
        .toList();
  }

  private void checkIfGuestsAlreadyInBooking(Booking booking, List<String> inputIdCardNumbers) {
    List<Guest> guestsInBooking = guestRepository.getAllGuestsByBookingIdAndIdCardNumberList(booking.getId(), inputIdCardNumbers);
    if (!guestsInBooking.isEmpty()) {
      throw new EntityAlreadyExistsException(String.format("Guest already registered in booking with id %s", booking.getId()));
    }
  }

  private static RegisterVisitorsOutput createOutput() {
    return RegisterVisitorsOutput.builder()
        .build();
  }
}
