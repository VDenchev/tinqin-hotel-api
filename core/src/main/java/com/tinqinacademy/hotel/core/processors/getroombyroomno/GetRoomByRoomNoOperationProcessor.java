package com.tinqinacademy.hotel.core.processors.getroombyroomno;

import com.tinqinacademy.hotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.models.output.RoomOutput;
import com.tinqinacademy.hotel.api.operations.getroombyroomno.input.GetRoomByRoomNoInput;
import com.tinqinacademy.hotel.api.operations.getroombyroomno.operation.GetRoomByRoomNoOperation;
import com.tinqinacademy.hotel.api.operations.getroombyroomno.output.GetRoomByRoomNoOutput;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static io.vavr.API.Match;

@Service
@Slf4j
public class GetRoomByRoomNoOperationProcessor extends BaseOperationProcessor implements GetRoomByRoomNoOperation {

  private final RoomRepository roomRepository;

  public GetRoomByRoomNoOperationProcessor(ConversionService conversionService, Validator validator, RoomRepository roomRepository) {
    super(conversionService, validator);
    this.roomRepository = roomRepository;
  }

  @Override
  public Either<ErrorOutput, GetRoomByRoomNoOutput> process(GetRoomByRoomNoInput input) {
    return validateInput(input)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start getRoom input: {}", validInput);

                  Room room = getRoomOrThrow(validInput);

                  List<LocalDate> datesOccupied = room.getBookings().stream()
                      .flatMap(b -> b.getStartDate().datesUntil(b.getEndDate().plusDays(1)))
                      .toList();

                  RoomOutput roomOutput = convertRoomToRoomOutput(room, datesOccupied);

                  GetRoomByRoomNoOutput output = createOutput(roomOutput);
                  log.info("End getRoom output: {}", output);
                  return output;
                })
                .toEither()
                .mapLeft(t -> Match(t).of(
                    customStatusCase(t, EntityNotFoundException.class, HttpStatus.BAD_REQUEST),
                    customStatusCase(t, IllegalArgumentException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                    defaultCase(t)
                ))
        );
  }

  private GetRoomByRoomNoOutput createOutput(RoomOutput roomOutput) {
    return GetRoomByRoomNoOutput.builder()
        .roomOutput(roomOutput)
        .build();
  }

  private Room getRoomOrThrow(GetRoomByRoomNoInput input) {
    return roomRepository.findRoomByNumber(input.getRoomNo())
        .orElseThrow(() -> new EntityNotFoundException("Room", "roomNo", input.getRoomNo()));
  }

  private RoomOutput convertRoomToRoomOutput(Room room, List<LocalDate> datesOccupied) {
    RoomOutput output = conversionService.convert(room, RoomOutput.class);

    List<Bed> beds = room.getBeds();

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
