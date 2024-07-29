package com.tinqinacademy.hotel.core.operations.partialupdateroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartialUpdateRoomOperationImpl implements PartialUpdateRoomOperation {
  
  private final RoomRepository roomRepository;
  private final BedRepository bedRepository;
  private final ConversionService conversionService;
  private final ObjectMapper objectMapper;

  @Override
  public PartialUpdateRoomOutput process(PartialUpdateRoomInput input) {
    log.info("Start partialUpdateRoom input: {}", input);

    Room savedRoom = getRoomByIdOrThrow(input);

    RoomInput roomInput = input.getRoomInput();
    Room partialRoom = convertPartialInputToRoom(input.getRoomId(), roomInput);

    try {
      JsonObject savedRoomValue = convertToJsonObject(savedRoom);
      JsonObject patchRoomValue = convertToJsonObject(partialRoom);

      JsonValue result = Json.createMergePatch(patchRoomValue).apply(savedRoomValue);
      Room updatedRoom = objectMapper.readValue(result.toString(), Room.class);
      log.info("Merge patch json value: {}", updatedRoom);

      roomRepository.save(updatedRoom);
    } catch (JsonProcessingException e) {
      //TODO: handle exception
      throw new RuntimeException(e);
    }

    PartialUpdateRoomOutput output = convertRoomToRoomOutput(savedRoom);
    log.info("End partialUpdateRoom output: {}", output);
    return output;
  }

  private Room getRoomByIdOrThrow(PartialUpdateRoomInput input) {
    return roomRepository.findById(input.getRoomId())
        .orElseThrow(() -> new EntityNotFoundException("Room", input.getRoomId()));
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
            .findByBedSize(BedSize.getByCode(b.getCode()))
            //TODO: throw custom exception (this will (almost) never fail but w/e)
            .orElseThrow()
        )
    );
    return beds;
  }

  private JsonObject convertToJsonObject(Object object) throws JsonProcessingException {
    return Json.createReader(
        new StringReader(objectMapper.writeValueAsString(object))
    ).readObject();
  }

  private PartialUpdateRoomOutput convertRoomToRoomOutput(Room room) {
    return PartialUpdateRoomOutput.builder()
        .id(room.getId())
        .build();
  }
}
