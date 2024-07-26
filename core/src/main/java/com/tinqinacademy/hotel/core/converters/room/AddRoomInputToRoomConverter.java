package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddRoomInputToRoomConverter extends BaseConverter<AddRoomInput, Room> {

  private final RoomInputToRoomConverter roomInputToRoomConverter;

  @Override
  public Room doConvert(AddRoomInput source) {
    Room result = roomInputToRoomConverter.convert(source.getRoomInput());
    return result;
  }
}
