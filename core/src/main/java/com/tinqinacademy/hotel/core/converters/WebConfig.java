package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.core.converters.booking.BookRoomInputToBooking;
import com.tinqinacademy.hotel.core.converters.enums.PersBathroomTypeToApiBathroomType;
import com.tinqinacademy.hotel.core.converters.enums.BedToBedType;
import com.tinqinacademy.hotel.core.converters.enums.ApiBathroomTypeToPersistenceBathroomType;
import com.tinqinacademy.hotel.core.converters.room.AddRoomInputToRoom;
import com.tinqinacademy.hotel.core.converters.room.PartialUpdateRoomInputToRoom;
import com.tinqinacademy.hotel.core.converters.room.RoomInputToRoom;
import com.tinqinacademy.hotel.core.converters.room.RoomToGetRoomOutput;
import com.tinqinacademy.hotel.core.converters.room.UpdateRoomInputToRoom;
import com.tinqinacademy.hotel.core.converters.visitors.VisitorDetailsInputToGuest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final RoomInputToRoom roomInputToRoom;
  private final ApiBathroomTypeToPersistenceBathroomType apiBathroomTypeToPersistenceBathroomType;
  private final AddRoomInputToRoom addRoomInputToRoom;
  private final UpdateRoomInputToRoom updateRoomInputToRoom;
  private final PartialUpdateRoomInputToRoom partialUpdateRoomInputToRoom;
  private final PersBathroomTypeToApiBathroomType persBathroomTypeToApiBathroomType;
  private final BedToBedType bedToBedType;
  private final RoomToGetRoomOutput roomToGetRoomOutput;
  private final BookRoomInputToBooking bookRoomInputToBooking;
  private final VisitorDetailsInputToGuest visitorDetailsInputToGuest;

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(roomInputToRoom);
    registry.addConverter(bedToBedType);
    registry.addConverter(apiBathroomTypeToPersistenceBathroomType);
    registry.addConverter(persBathroomTypeToApiBathroomType);
    registry.addConverter(addRoomInputToRoom);
    registry.addConverter(updateRoomInputToRoom);
    registry.addConverter(partialUpdateRoomInputToRoom);
    registry.addConverter(roomToGetRoomOutput);
    registry.addConverter(bookRoomInputToBooking);
    registry.addConverter(visitorDetailsInputToGuest);
  }
}
