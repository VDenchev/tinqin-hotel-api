package com.tinqinacademy.hotel.core.converters.room;


import com.tinqinacademy.hotel.api.models.input.RoomInput;
import com.tinqinacademy.hotel.core.converters.enums.PersistenceBathroomTypeConverter;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomInputToRoomConverter implements Converter<RoomInput, Room> {

  private final PersistenceBathroomTypeConverter persistenceBathroomTypeConverter;

  @Override
  public Room convert(RoomInput source) {
    return Room.builder()
        .number(source.getNumber())
        .floor(source.getFloor())
        .price(source.getPrice())
        .bathroomType(
            persistenceBathroomTypeConverter.convert(source.getBathroomType())
        ).build();
  }
}
