package com.tinqinacademy.hotel.core.processors.registervisitors;

import com.tinqinacademy.hotel.api.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.DuplicateInputException;
import com.tinqinacademy.hotel.api.exceptions.EntityAlreadyExistsException;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.exceptions.ExceedsRoomBedsCapacityException;
import com.tinqinacademy.hotel.api.exceptions.VisitorDateMismatchException;
import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.input.RegisterVisitorsInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.operation.RegisterVisitorsOperation;
import com.tinqinacademy.hotel.api.operations.registervisitors.output.RegisterVisitorsOutput;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.guest.Guest;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    return validateInput(input)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start registerVisitors input: {}", validInput);
                  UUID bookingId = UUID.fromString(validInput.getBookingId());
                  Booking booking = bookingRepository.findById(bookingId)
                      .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));

                  List<VisitorDetailsInput> inputVisitors = validInput.getVisitors();
                  validateVisitors(inputVisitors, booking);

                  Set<String> inputIdCardNumbers = inputVisitors.stream()
                      .map(VisitorDetailsInput::getIdCardNo)
                      .collect(Collectors.toSet());

                  checkForDuplicateVisitorsInInput(inputVisitors, inputIdCardNumbers);
                  checkIfGuestsAlreadyInBooking(booking, inputIdCardNumbers);

                  List<Guest> existingGuests = guestRepository.getAllGuestsByIdCardNumberCollection(inputIdCardNumbers);
                  List<Guest> guestsToSave = getNotYetPersistedVisitors(existingGuests,
                      inputVisitors);

                  int totalVisitors = booking.getGuests().size() + inputVisitors.size();
                      checkIfMaximumCapacityReached(booking, totalVisitors);

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
                    customStatusCase(t, DuplicateInputException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                    customStatusCase(t, EntityNotFoundException.class, HttpStatus.NOT_FOUND),
                    customStatusCase(t, ExceedsRoomBedsCapacityException.class, HttpStatus.BAD_REQUEST),
                    customStatusCase(t, EntityAlreadyExistsException.class, HttpStatus.CONFLICT),
                    customStatusCase(t, IllegalArgumentException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                    defaultCase(t)
                ))
        );
  }

  private static void checkIfMaximumCapacityReached(
      Booking booking, int totalVisitors
  ) throws ExceedsRoomBedsCapacityException {
    List<Bed> roomBeds = booking.getRoom().getBeds();
    Integer totalBedCapacity = roomBeds.stream()
        .map(Bed::getCapacity)
        .reduce(0, Integer::sum);
    if (totalVisitors > totalBedCapacity) {
      throw new ExceedsRoomBedsCapacityException(String.format("Maximum room capacity of %d reached", totalBedCapacity));
    }
  }

  private static void checkForDuplicateVisitorsInInput(
      List<VisitorDetailsInput> inputVisitors,
      Set<String> inputIdCardNumbers
  ) throws DuplicateInputException {
    if (inputIdCardNumbers.size() != inputVisitors.size()) {
      throw new DuplicateInputException("Visitors must have unique id card numbers");
    }
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

  private void checkIfGuestsAlreadyInBooking(Booking booking, Set<String> inputIdCardNumbers) {
    List<Guest> guestsInBooking = guestRepository.getAllGuestsByBookingIdAndIdCardNumberCollection(booking.getId(), inputIdCardNumbers);
    if (!guestsInBooking.isEmpty()) {
      throw new EntityAlreadyExistsException(String.format("Guest already registered in booking with id %s", booking.getId()));
    }
  }

  private static RegisterVisitorsOutput createOutput() {
    return RegisterVisitorsOutput.builder()
        .build();
  }
}
