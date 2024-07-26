package com.tinqinacademy.hotel.core.mappers;

import com.tinqinacademy.hotel.api.operations.bookroom.input.BookRoomInput;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.entities.user.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, builder = @Builder(disableBuilder = true))
public interface BookingMapper {

  @Mapping(target = "room", source ="room")
  @Mapping(target = "user", source ="user")
  @Mapping(target = ".", source ="bookRoomInput")
  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  Booking fromBookRoomInputToBooking(BookRoomInput bookRoomInput, Room room, User user);
}
