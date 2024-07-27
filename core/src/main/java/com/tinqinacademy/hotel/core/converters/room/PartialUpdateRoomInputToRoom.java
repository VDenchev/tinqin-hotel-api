package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.partialupdateroom.input.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartialUpdateRoomInputToRoom extends BaseConverter<PartialUpdateRoomInput, Room> {

  private final RoomInputToRoom roomInputToRoom;

  @Override
  public Room doConvert(PartialUpdateRoomInput source) {
    Room room = roomInputToRoom.convert(source.getRoomInput());
    room.setId(source.getRoomId());
    return room;
  }
}
