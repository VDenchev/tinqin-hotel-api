package com.tinqinacademy.hotel.core.operations.getroom;

import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.models.output.DatesOccupied;
import com.tinqinacademy.hotel.api.models.output.RoomOutput;
import com.tinqinacademy.hotel.api.operations.getroom.input.RoomDetailsInput;
import com.tinqinacademy.hotel.api.operations.getroom.operation.GetRoomOperation;
import com.tinqinacademy.hotel.api.operations.getroom.output.RoomDetailsOutput;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetRoomOperationImpl implements GetRoomOperation {

  private final RoomRepository roomRepository;
  private final ConversionService conversionService;

  @Override
  public RoomDetailsOutput process(RoomDetailsInput input) {
    log.info("Start getRoom input: {}", input);

    Room room = getRoomOrThrow(input.getRoomId());

    List<LocalDate> dates = room.getBookings().stream()
        .flatMap(b -> b.getStartDate().datesUntil(b.getEndDate().plusDays(1)))
        .toList();
    DatesOccupied datesOccupied = convertDateListToDatesOccupied(dates);
    List<Bed> beds = roomRepository.getAllBedsByRoomId(room.getId());

    RoomOutput roomOutput = convertRoomToRoomOutput(room, beds, datesOccupied);

    RoomDetailsOutput output = createOutput(roomOutput);
    log.info("End getRoom output: {}", output);
    return output;
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
