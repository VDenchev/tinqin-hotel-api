package com.tinqinacademy.hotel.core.processors.getroom;

import com.tinqinacademy.hotel.api.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.models.output.DatesOccupied;
import com.tinqinacademy.hotel.api.models.output.RoomOutput;
import com.tinqinacademy.hotel.api.operations.getroom.input.RoomDetailsInput;
import com.tinqinacademy.hotel.api.operations.getroom.operation.GetRoomOperation;
import com.tinqinacademy.hotel.api.operations.getroom.output.RoomDetailsOutput;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.vavr.API.Match;

@Service
@Slf4j
public class GetRoomOperationProcessor extends BaseOperationProcessor implements GetRoomOperation {

  private final RoomRepository roomRepository;

  public GetRoomOperationProcessor(
      ConversionService conversionService,
      Validator validator, RoomRepository roomRepository
  ) {
    super(conversionService, validator);
    this.roomRepository = roomRepository;
  }


  @Override
  public Either<ErrorOutput, RoomDetailsOutput> process(RoomDetailsInput input) {
    return validateInput(input)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start getRoom input: {}", validInput);
                  UUID roomId = UUID.fromString(validInput.getRoomId());

                  Room room = getRoomOrThrow(roomId);

                  List<LocalDate> dates = room.getBookings().stream()
                      .flatMap(b -> b.getStartDate().datesUntil(b.getEndDate().plusDays(1)))
                      .toList();
                  DatesOccupied datesOccupied = convertDateListToDatesOccupied(dates);
                  List<Bed> beds = roomRepository.getAllBedsByRoomId(room.getId());

                  RoomOutput roomOutput = convertRoomToRoomOutput(room, beds, datesOccupied);

                  RoomDetailsOutput output = createOutput(roomOutput);
                  log.info("End getRoom output: {}", output);
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

  private RoomDetailsOutput createOutput(RoomOutput roomOutput) {
    return RoomDetailsOutput.builder()
        .roomOutput(roomOutput)
        .build();
  }

  private DatesOccupied convertDateListToDatesOccupied(List<LocalDate> dates) {
    return DatesOccupied.builder()
        .dates(dates)
        .build();
  }

  private Room getRoomOrThrow(UUID roomId) {
    return roomRepository.findById(roomId)
        .orElseThrow(() -> new EntityNotFoundException("Room", roomId));
  }

  private RoomOutput convertRoomToRoomOutput(Room room, List<Bed> beds, DatesOccupied datesOccupied) {
    RoomOutput output = conversionService.convert(room, RoomOutput.class);
    output.setBedSizes(beds.stream()
        .map(b -> conversionService.convert(b, BedType.class))
        .toList());
    output.setBathroomType(conversionService.convert(room.getBathroomType(),
        com.tinqinacademy.hotel.api.enums.BathroomType.class));
    output.setBedCount(beds.size());
    output.setDatesOccupied(datesOccupied);
    return output;
  }
}
