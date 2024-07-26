package com.tinqinacademy.hotel.core.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.exceptions.BedDoesNotExistException;
import com.tinqinacademy.hotel.api.exceptions.VisitorDateMismatchException;
import com.tinqinacademy.hotel.api.exceptions.EntityAlreadyExistsException;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.exceptions.InvalidUUIDException;
import com.tinqinacademy.hotel.api.models.input.RoomInput;
import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import com.tinqinacademy.hotel.api.models.output.VisitorDetailsOutput;
import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.output.AddRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.input.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.output.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.input.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.output.PartialUpdateOutput;
import com.tinqinacademy.hotel.api.operations.registervisitors.input.RegisterVisitorsInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.output.RegisterVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.input.SearchVisitorsInput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.output.SearchVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.input.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.output.UpdateRoomOutput;
import com.tinqinacademy.hotel.api.services.contracts.SystemService;
import com.tinqinacademy.hotel.core.mappers.RoomMapper;
import com.tinqinacademy.hotel.core.mappers.VisitorMapper;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.guest.Guest;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.models.output.VisitorSearchResult;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import com.tinqinacademy.hotel.persistence.repositories.CustomGuestRepository;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

  private final RoomRepository roomRepository;
  private final BedRepository bedRepository;
  private final VisitorMapper visitorMapper;
  private final BookingRepository bookingRepository;
  private final GuestRepository guestRepository;
  private final CustomGuestRepository customGuestRepository;
  private final ObjectMapper objectMapper;
  private final ConversionService conversionService;

  @Override
  public RegisterVisitorsOutput registerVisitors(RegisterVisitorsInput input) {
    log.info("Start registerVisitors input: {}", input);

    Booking booking = bookingRepository.findById(input.getBookingId())
        .orElseThrow(() -> new EntityNotFoundException("Booking", input.getBookingId()));

    boolean validVisitors = input.getVisitors().stream()
        .allMatch(v -> checkVisitorValidity(v, booking));

    if (!validVisitors) {
      throw new VisitorDateMismatchException("Visitor start and end dates must match the booking dates");
    }

    List<Guest> guests = visitorMapper.fromRegisterVisitorsInputToGuestList(input);
    log.info("Guests: {}", guests);

    guests = guestRepository.saveAll(guests);
    booking.setGuests(guests);
    bookingRepository.save(booking);

    RegisterVisitorsOutput output = RegisterVisitorsOutput.builder()
        .build();

    log.info("End registerVisitors output: {}", output);
    return output;
  }

  private Boolean checkVisitorValidity(VisitorDetailsInput visitor, Booking booking) {
    return (visitor.getStartDate().isBefore(visitor.getEndDate()) || visitor.getStartDate().isEqual(visitor.getEndDate()))
        && (visitor.getStartDate().isAfter(booking.getStartDate()) || visitor.getStartDate().isEqual(booking.getStartDate()))
        && (visitor.getEndDate().isBefore(booking.getEndDate()) || visitor.getEndDate().isEqual(booking.getEndDate()));
  }

  @Override
  public SearchVisitorsOutput searchVisitors(SearchVisitorsInput input) {
    log.info("Start searchVisitors input: {}", input);

    VisitorDetailsInput visitorDetailsInput = input.getVisitorDetailsInput();

    List<VisitorSearchResult> results = customGuestRepository.searchVisitors(
        visitorDetailsInput.getStartDate(),
        visitorDetailsInput.getEndDate(),
        visitorDetailsInput.getFirstName(),
        visitorDetailsInput.getLastName(),
        visitorDetailsInput.getBirthDate(),
        visitorDetailsInput.getPhoneNumber(),
        visitorDetailsInput.getIdCardNumber(),
        visitorDetailsInput.getIdCardIssueAuthority(),
        input.getRoomNumber()
    );

    List<VisitorDetailsOutput> visitors = results.stream().map(r ->
        VisitorDetailsOutput.builder()
            .startDate(r.getStartDate())
            .endDate(r.getEndDate())
            .firstName(r.getFirstName())
            .birthDate(r.getBirthDate())
            .lastName(r.getLastName())
            .idCardNumber(r.getIdCardNumber())
            .idCardValidity(r.getIdCardValidity())
            .idCardIssueAuthority(r.getIdCardIssueAuthority())
            .idCardIssueDate(r.getIdCardIssueDate())
            .build()).toList();

    SearchVisitorsOutput output = SearchVisitorsOutput.builder()
        .visitors(visitors)
        .build();

    log.info("End searchVisitors output: {}", output);
    return output;
  }

  @Override
  @Transactional
  public AddRoomOutput addRoom(AddRoomInput input) {
    log.info("Start addRoom input: {}", input);

    Optional<Room> roomWithTheSameNumber = roomRepository.findRoomByNumber(input.getRoomInput().getNumber());
    if (roomWithTheSameNumber.isPresent()) {
      throw new EntityAlreadyExistsException("Room with room number " +
          input.getRoomInput().getNumber() + " already exists!"
      );
    }
    List<Bed> beds = getBedEntitiesFromRoomInput(input.getRoomInput());

    Room roomToAdd = conversionService.convert(input, Room.class);
    roomToAdd.setBeds(beds);
    log.info("Mapping addRoom room: {}", roomToAdd);

    roomRepository.save(roomToAdd);

    AddRoomOutput output = AddRoomOutput.builder()
        .id(roomToAdd.getId())
        .build();

    log.info("End addRoom output: {}", output);
    return output;
  }

  private List<Bed> getBedEntitiesFromRoomInput(RoomInput input) {
    List<Bed> beds = new ArrayList<>();
    input.getBeds().forEach(b ->
        beds.add(bedRepository
            .findByBedSize(BedSize.getByCode(b.getCode()))
            //TODO: throw custom exception (this will (almost) never fail but w/e)
            .orElseThrow()
        )
    );
    return beds;
  }

  @Override
  public UpdateRoomOutput updateRoom(UpdateRoomInput input) {
    log.info("Start updateRoom input: {}", input);

    roomRepository.findById(input.getId())
        .orElseThrow(() -> new EntityNotFoundException("Room", input.getId()));

    List<Bed> beds = getBedEntitiesFromRoomInput(input.getRoomInput());

    Room roomToUpdate = conversionService.convert(input.getRoomInput(), Room.class);
    roomToUpdate.setBeds(beds);
    log.info("Mapping updateRoom room: {}", roomToUpdate);

    roomRepository.save(roomToUpdate);

    UpdateRoomOutput output = UpdateRoomOutput.builder()
        .id(roomToUpdate.getId())
        .build();

    log.info("End updateRoom output: {}", output);
    return output;
  }

  @Override
  public PartialUpdateOutput partialUpdateRoom(PartialUpdateRoomInput input) {
    log.info("Start partialUpdateRoom input: {}", input);

    Room savedRoom = roomRepository.findById(input.getRoomId())
        .orElseThrow(() -> new EntityNotFoundException("Room", input.getRoomId()));
    Room partialRoom = conversionService.convert(input.getRoomInput(), Room.class);
    List<Bed> beds = null;
    if (input.getRoomInput().getBeds() != null) {
      beds = getBedEntitiesFromRoomInput(input.getRoomInput());
    }
    partialRoom.setBeds(beds);

    try {
      JsonObject savedRoomValue = Json.createReader(
          new StringReader(objectMapper.writeValueAsString(savedRoom))
      ).readObject();
      JsonObject patchRoomValue = Json.createReader(
          new StringReader(objectMapper.writeValueAsString(partialRoom))
      ).readObject();

      JsonValue result = Json.createMergePatch(patchRoomValue).apply(savedRoomValue);
      Room updatedRoom = objectMapper.readValue(result.toString(), Room.class);
      log.info("Merge patch json value: {}", updatedRoom);

      roomRepository.save(updatedRoom);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    PartialUpdateOutput output = PartialUpdateOutput.builder()
        .id(savedRoom.getId())
        .build();

    log.info("End partialUpdateRoom output: {}", output);
    return output;
  }

  @Override
  public DeleteRoomOutput deleteRoom(DeleteRoomInput input) {
    log.info("Start deleteRoom input: {}", input);

    Room room = roomRepository.findById(input.getId())
        .orElseThrow(() -> new EntityNotFoundException("Room", input.getId()));

//      List<Booking> roomBookings = bookingRepository.getBetweenDatesForRoom(room.getId(), LocalDate.now(), LocalDate.MAX);
//
//      if (!roomBookings.isEmpty()) {
//        throw new RoomUnavailableException("Unable to delete room: Room is still being used");
//      }

//      room.getBookings().forEach(b -> ); bookingRepository.delete();
    roomRepository.delete(room);

    DeleteRoomOutput output = DeleteRoomOutput.builder()
        .build();

    log.info("End deleteRoom output: {}", output);

    return output;
  }
}
