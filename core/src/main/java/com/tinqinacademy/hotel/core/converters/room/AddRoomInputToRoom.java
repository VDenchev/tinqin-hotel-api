package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddRoomInputToRoom extends BaseConverter<AddRoomInput, Room> {

  private final RoomInputToRoom roomInputToRoom;

  @Override
  public Room doConvert(AddRoomInput source) {
    return roomInputToRoom.convert(source.getRoomInput());
  }
}
