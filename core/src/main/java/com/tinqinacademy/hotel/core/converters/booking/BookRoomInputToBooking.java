package com.tinqinacademy.hotel.core.converters.booking;

import com.tinqinacademy.hotel.api.operations.bookroom.input.BookRoomInput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookRoomInputToBooking extends BaseConverter<BookRoomInput, Booking> {
  @Override
  protected Booking doConvert(BookRoomInput source) {
    return Booking.builder()
        .startDate(source.getStartDate())
        .endDate(source.getEndDate())
        .build();
  }
}
