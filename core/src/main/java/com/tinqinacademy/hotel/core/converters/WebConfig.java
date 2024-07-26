package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.core.converters.booking.BookRoomInputToBooking;
import com.tinqinacademy.hotel.core.converters.enums.ApiBathroomTypeConverter;
import com.tinqinacademy.hotel.core.converters.enums.BedToApiBedSizeConverter;
import com.tinqinacademy.hotel.core.converters.enums.PersistenceBathroomTypeConverter;
import com.tinqinacademy.hotel.core.converters.room.AddRoomInputToRoomConverter;
import com.tinqinacademy.hotel.core.converters.room.PartialUpdateRoomInputToRoomConverter;
import com.tinqinacademy.hotel.core.converters.room.RoomInputToRoomConverter;
import com.tinqinacademy.hotel.core.converters.room.RoomToGetRoomOutputConverter;
import com.tinqinacademy.hotel.core.converters.room.UpdateRoomInputToRoomConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final RoomInputToRoomConverter roomInputToRoomConverter;
  private final PersistenceBathroomTypeConverter persistenceBathroomTypeConverter;
  private final AddRoomInputToRoomConverter addRoomInputToRoomConverter;
  private final UpdateRoomInputToRoomConverter updateRoomInputToRoomConverter;
  private final PartialUpdateRoomInputToRoomConverter partialUpdateRoomInputToRoomConverter;
  private final ApiBathroomTypeConverter apiBathroomTypeConverter;
  private final BedToApiBedSizeConverter bedToApiBedSizeConverter;
  private final RoomToGetRoomOutputConverter roomToGetRoomOutputConverter;
  private final BookRoomInputToBooking bookRoomInputToBooking;

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(roomInputToRoomConverter);
    registry.addConverter(bedToApiBedSizeConverter);
    registry.addConverter(persistenceBathroomTypeConverter);
    registry.addConverter(apiBathroomTypeConverter);
    registry.addConverter(addRoomInputToRoomConverter);
    registry.addConverter(updateRoomInputToRoomConverter);
    registry.addConverter(partialUpdateRoomInputToRoomConverter);
    registry.addConverter(roomToGetRoomOutputConverter);
    registry.addConverter(bookRoomInputToBooking);
  }
}
