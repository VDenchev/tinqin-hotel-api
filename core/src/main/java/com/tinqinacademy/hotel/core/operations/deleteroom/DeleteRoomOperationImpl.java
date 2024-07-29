package com.tinqinacademy.hotel.core.operations.deleteroom;

import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.operations.deleteroom.input.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.operation.DeleteRoomOperation;
import com.tinqinacademy.hotel.api.operations.deleteroom.output.DeleteRoomOutput;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteRoomOperationImpl implements DeleteRoomOperation {

  private final RoomRepository roomRepository;
  private final BookingRepository bookingRepository;

  @Override
  public DeleteRoomOutput process(DeleteRoomInput input) {
    log.info("Start deleteRoom input: {}", input);

    Room room = getRoomByIdOrThrow(input.getId());

//      if (!roomBookings.isEmpty()) {
//        throw new RoomUnavailableException("Unable to delete room: Room is still being used");
//      }

    bookingRepository.deleteBookingsByRoom(room);
    roomRepository.delete(room);

    DeleteRoomOutput output = createOutput();
    log.info("End deleteRoom output: {}", output);
    return output;
  }

  private DeleteRoomOutput createOutput() {
    return DeleteRoomOutput.builder()
        .build();
  }

  private Room getRoomByIdOrThrow(UUID roomId) {
    return roomRepository.findById(roomId)
        .orElseThrow(() -> new EntityNotFoundException("Room", roomId));
  }
}
