package com.tinqinacademy.hotel.api.services.contracts;

import com.tinqinacademy.hotel.api.operations.bookroom.input.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.output.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.input.AvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.output.AvailableRoomsOutput;
import com.tinqinacademy.hotel.api.operations.getroom.input.RoomDetailsInput;
import com.tinqinacademy.hotel.api.operations.getroom.output.RoomDetailsOutput;
import com.tinqinacademy.hotel.api.operations.removebooking.input.RemoveBookingInput;
import com.tinqinacademy.hotel.api.operations.removebooking.output.RemoveBookingOutput;
import org.springframework.stereotype.Service;

@Service
public interface HotelService {

  BookRoomOutput bookRoom(BookRoomInput input);
  AvailableRoomsOutput checkAvailableRooms(AvailableRoomsInput input);
  RoomDetailsOutput getRoom(RoomDetailsInput input);
  RemoveBookingOutput removeBooking(RemoveBookingInput input);
}
