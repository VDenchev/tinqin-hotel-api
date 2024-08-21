package com.tinqinacademy.hotel.core.processors.partialupdateroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.BedDoesNotExistException;
import com.tinqinacademy.hotel.api.exceptions.EntityAlreadyExistsException;
import com.tinqinacademy.hotel.api.validation.groups.NonMandatoryFieldsGroup;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.models.input.RoomInput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.input.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.operation.PartialUpdateRoomOperation;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.output.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.Match;

@Service
@Slf4j
public class PartialUpdateRoomOperationProcessor extends BaseOperationProcessor implements PartialUpdateRoomOperation {

  private final RoomRepository roomRepository;
  private final BedRepository bedRepository;
  private final ObjectMapper objectMapper;

  public PartialUpdateRoomOperationProcessor(
      ConversionService conversionService, Validator validator,
      RoomRepository roomRepository, BedRepository bedRepository, ObjectMapper objectMapper
  ) {
    super(conversionService, validator);
    this.roomRepository = roomRepository;
    this.bedRepository = bedRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public Either<ErrorOutput, PartialUpdateRoomOutput> process(PartialUpdateRoomInput input) {
    return validateInput(input, NonMandatoryFieldsGroup.class)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start partialUpdateRoom input: {}", validInput);

                  UUID roomId = UUID.fromString(validInput.getRoomId());
                  checkForDuplicateRoomNumber(validInput, roomId);
                  Room savedRoom = getRoomByIdOrThrow(roomId);

                  RoomInput roomInput = validInput.getRoomInput();
                  Room partialRoom = convertPartialInputToRoom(roomId, roomInput);

                  JsonObject savedRoomValue = convertToJsonObject(savedRoom);
                  JsonObject patchRoomValue = convertToJsonObject(partialRoom);

                  JsonValue result = Json.createMergePatch(patchRoomValue).apply(savedRoomValue);
                  Room updatedRoom = objectMapper.readValue(result.toString(), Room.class);
                  log.info("Merge patch json value: {}", updatedRoom);

                  roomRepository.save(updatedRoom);

                  PartialUpdateRoomOutput output = convertRoomToRoomOutput(savedRoom);
                  log.info("End partialUpdateRoom output: {}", output);
                  return output;
                })
                .toEither()
                .mapLeft(t -> Match(t).of(
                    customStatusCase(t, EntityNotFoundException.class, HttpStatus.NOT_FOUND),
                    customStatusCase(t, EntityAlreadyExistsException.class, HttpStatus.CONFLICT),
                    customStatusCase(t, JsonProcessingException.class, HttpStatus.BAD_REQUEST),
                    customStatusCase(t, BedDoesNotExistException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                    customStatusCase(t, IllegalArgumentException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                    defaultCase(t)
                ))
        );
  }

  private Room getRoomByIdOrThrow(UUID roomId) {
    return roomRepository.findById(roomId)
        .orElseThrow(() -> new EntityNotFoundException("Room", "id", roomId.toString()));
  }

  private Room convertPartialInputToRoom(UUID roomId, RoomInput roomInput) {
    Room partialRoom = conversionService.convert(roomInput, Room.class);

    partialRoom.setId(roomId);
    partialRoom.setBathroomType(conversionService.convert(roomInput.getBathroomType(), BathroomType.class));

    List<Bed> beds = null;
    if (roomInput.getBedSizes() != null) {
      beds = getBedEntitiesFromRoomInput(roomInput);
    }
    partialRoom.setBeds(beds);

    return partialRoom;
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

  private JsonObject convertToJsonObject(Object object) throws JsonProcessingException {
    return Json.createReader(
        new StringReader(objectMapper.writeValueAsString(object))
    ).readObject();
  }

  private void checkForDuplicateRoomNumber(PartialUpdateRoomInput input, UUID roomId) {
    String roomNo = input.getRoomInput().getRoomNo();
    if (roomNo == null || roomNo.isBlank()) {
      return;
    }
    Optional<Room> maybeRoomDuplicateRoomNo = roomRepository.findRoomByNumber(input.getRoomInput().getRoomNo());

    if (maybeRoomDuplicateRoomNo.isPresent() && !maybeRoomDuplicateRoomNo.get().getId().equals(roomId)) {
      throw new EntityAlreadyExistsException(
          String.format("Room with number %s already exists", maybeRoomDuplicateRoomNo.get().getNumber())
      );
    }
  }

  private PartialUpdateRoomOutput convertRoomToRoomOutput(Room room) {
    return PartialUpdateRoomOutput.builder()
        .id(room.getId().toString())
        .build();
  }
}
