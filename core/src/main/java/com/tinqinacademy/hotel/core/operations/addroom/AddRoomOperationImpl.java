package com.tinqinacademy.hotel.core.operations.addroom;

import com.tinqinacademy.hotel.api.exceptions.EntityAlreadyExistsException;
import com.tinqinacademy.hotel.api.models.input.RoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.operation.AddRoomOperation;
import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.output.AddRoomOutput;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddRoomOperationImpl implements AddRoomOperation {

  private final RoomRepository roomRepository;
  private final BedRepository bedRepository;
  private final ConversionService conversionService;

  @Override
  @Transactional
  public AddRoomOutput process(AddRoomInput input) {
    log.info("Start addRoom input: {}", input);

    RoomInput roomInput = input.getRoomInput();

    checkForExistingRoomWithTheSameNumber(roomInput.getRoomNo());

    List<Bed> beds = getBedEntitiesFromRoomInput(roomInput);
    Room roomToAdd = convertRoomInputToRoom(roomInput, beds);

    Room persistedRoom = roomRepository.save(roomToAdd);

    AddRoomOutput output = convertRoomToRoomOutput(persistedRoom);
    log.info("End addRoom output: {}", output);
    return output;
  }


  private void checkForExistingRoomWithTheSameNumber(String roomNo) {
    Optional<Room> roomWithTheSameNumber = roomRepository.findRoomByNumber(roomNo);
    if (roomWithTheSameNumber.isPresent()) {
      throw new EntityAlreadyExistsException("Room with room number " +
          roomNo + " already exists!"
      );
    }
  }

  private Room convertRoomInputToRoom(RoomInput roomInput, List<Bed> beds) {
    Room room = conversionService.convert(roomInput, Room.class);
    room.setBeds(beds);
    room.setBathroomType(conversionService.convert(roomInput.getBathroomType(), BathroomType.class));
    return room;
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

  private AddRoomOutput convertRoomToRoomOutput(Room room) {
     return AddRoomOutput.builder()
        .id(room.getId())
        .build();
  }
}
