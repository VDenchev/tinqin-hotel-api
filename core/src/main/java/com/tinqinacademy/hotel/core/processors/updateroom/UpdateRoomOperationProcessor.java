package com.tinqinacademy.hotel.core.processors.updateroom;

import com.tinqinacademy.hotel.api.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.BedDoesNotExistException;
import com.tinqinacademy.hotel.api.exceptions.EntityAlreadyExistsException;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.models.input.RoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.input.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.operation.UpdateRoomOperation;
import com.tinqinacademy.hotel.api.operations.updateroom.output.UpdateRoomOutput;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.Match;

@Service
@Slf4j
public class UpdateRoomOperationProcessor extends BaseOperationProcessor implements UpdateRoomOperation {

  private final RoomRepository roomRepository;
  private final BedRepository bedRepository;

  public UpdateRoomOperationProcessor(
      ConversionService conversionService, Validator validator,
      RoomRepository roomRepository, BedRepository bedRepository
  ) {
    super(conversionService, validator);
    this.roomRepository = roomRepository;
    this.bedRepository = bedRepository;
  }

  @Override
  @Transactional
  public Either<ErrorOutput, UpdateRoomOutput> process(UpdateRoomInput input) {
    return validateInput(input)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start updateRoom input: {}", validInput);

                  UUID roomId = getRoomIdOrThrow(validInput);
                  checkForDuplicateRoomNumber(validInput, roomId);

                  RoomInput roomInput = validInput.getRoomInput();
                  List<Bed> beds = getBedEntitiesFromRoomInput(roomInput);

                  Room roomToUpdate = convertRoomIntputToRoom(roomInput, roomId, beds);

                  roomRepository.save(roomToUpdate);

                  UpdateRoomOutput output = createOutput(roomToUpdate);

                  log.info("End updateRoom output: {}", output);
                  return output;
                })
                .toEither()
                .mapLeft(t -> Match(t).of(
                    customStatusCase(t, EntityNotFoundException.class, HttpStatus.NOT_FOUND),
                    customStatusCase(t, EntityAlreadyExistsException.class, HttpStatus.CONFLICT),
                    customStatusCase(t, BedDoesNotExistException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                    customStatusCase(t, IllegalArgumentException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                    defaultCase(t)
                ))
        );
  }

  private UUID getRoomIdOrThrow(UpdateRoomInput validInput) {
    UUID roomId = UUID.fromString(validInput.getRoomId());
    roomRepository.findById(roomId)
        .orElseThrow(() -> new EntityNotFoundException("Room", "id", validInput.getRoomId()));
    return roomId;
  }

  private void checkForDuplicateRoomNumber(UpdateRoomInput validInput, UUID roomId) {
    Optional<Room> maybeRoomDuplicateRoomNo = roomRepository.findRoomByNumber(validInput.getRoomInput().getRoomNo());

    if (maybeRoomDuplicateRoomNo.isPresent() && !maybeRoomDuplicateRoomNo.get().getId().equals(roomId)) {
      throw new EntityAlreadyExistsException(
          String.format("Room with number %s already exists", maybeRoomDuplicateRoomNo.get().getNumber())
      );
    }
  }

  private List<Bed> getBedEntitiesFromRoomInput(RoomInput input) {
    List<Bed> beds = new ArrayList<>();
    input.getBedSizes().forEach(b ->
        beds.add(bedRepository
            .findByBedSize(BedSize.getByCode(b))
            .orElseThrow(() -> new BedDoesNotExistException(b))
        )
    );
    return beds;
  }

  private Room convertRoomIntputToRoom(RoomInput roomInput, UUID roomId, List<Bed> beds) {
    Room roomToUpdate = conversionService.convert(roomInput, Room.class);
    roomToUpdate.setId(roomId);
    roomToUpdate.setBeds(beds);
    roomToUpdate.setBathroomType(conversionService.convert(roomInput.getBathroomType(), BathroomType.class));
    return roomToUpdate;
  }

  private UpdateRoomOutput createOutput(Room roomToUpdate) {
    return UpdateRoomOutput.builder()
        .id(roomToUpdate.getId().toString())
        .build();
  }
}
