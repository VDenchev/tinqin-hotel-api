package com.tinqinacademy.hotel.core.operations.updateroom;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateRoomOperationImpl implements UpdateRoomOperation {

  private final RoomRepository roomRepository;
  private final ConversionService conversionService;
  private final BedRepository bedRepository;

  @Override
  public UpdateRoomOutput process(UpdateRoomInput input) {
    log.info("Start updateRoom input: {}", input);

    roomRepository.findById(input.getRoomId())
        .orElseThrow(() -> new EntityNotFoundException("Room", input.getRoomId()));

    RoomInput roomInput = input.getRoomInput();
    List<Bed> beds = getBedEntitiesFromRoomInput(roomInput);

    Room roomToUpdate = convertRoomIntputToRoom(roomInput, beds);

    roomRepository.save(roomToUpdate);

    UpdateRoomOutput output = createOutput(roomToUpdate);

    log.info("End updateRoom output: {}", output);
    return output;
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

  private Room convertRoomIntputToRoom(RoomInput roomInput, List<Bed> beds) {
    Room roomToUpdate = conversionService.convert(roomInput, Room.class);
    roomToUpdate.setBeds(beds);
    roomToUpdate.setBathroomType(conversionService.convert(roomInput.getBathroomType(), BathroomType.class));
    return roomToUpdate;
  }

  private UpdateRoomOutput createOutput(Room roomToUpdate) {
    return UpdateRoomOutput.builder()
        .id(roomToUpdate.getId())
        .build();
  }
}
