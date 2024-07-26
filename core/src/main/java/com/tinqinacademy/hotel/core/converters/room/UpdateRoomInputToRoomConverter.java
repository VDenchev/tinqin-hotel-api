package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.updateroom.input.UpdateRoomInput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateRoomInputToRoomConverter extends BaseConverter<UpdateRoomInput, Room> {

  private final RoomInputToRoomConverter roomInputToRoomConverter;
  @Override
  public Room doConvert(UpdateRoomInput source) {
    Room result = roomInputToRoomConverter.convert(source.getRoomInput());
    result.setId(source.getId());

    return result;
  }
}
