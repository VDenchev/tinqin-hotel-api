package com.tinqinacademy.hotel.restexport.client;

import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.output.AddRoomOutput;
import com.tinqinacademy.hotel.api.operations.bookroom.input.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.output.AvailableRoomsOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.output.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.getroom.input.RoomDetailsInput;
import com.tinqinacademy.hotel.api.operations.getroom.output.RoomDetailsOutput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.input.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.output.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.registervisitors.input.RegisterVisitorsInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.output.RegisterVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.removebooking.output.RemoveBookingOutput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.output.SearchVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.input.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.output.UpdateRoomOutput;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.time.LocalDate;
import java.util.List;

import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.ADD_ROOM;
import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.BOOK_ROOM;
import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.CHECK_ROOM_AVAILABILITY;
import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.DELETE_ROOM;
import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.GET_ROOM;
import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.PARTIAL_UPDATE_ROOM;
import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.REGISTER_VISITORS;
import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.REMOVE_BOOKING;
import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.SEARCH_VISITORS;
import static com.tinqinacademy.hotel.api.FeignClientApiRoutes.UPDATE_ROOM;

@Headers({"Content-Type: application/json"})
public interface HotelClient {

  @RequestLine(CHECK_ROOM_AVAILABILITY)
  AvailableRoomsOutput checkAvailableRooms(
      @Param LocalDate startDate, @Param LocalDate endDate, @Param Integer bedCount,
      @Param List<String> bedSizes, @Param String bathroomType);

  @RequestLine(GET_ROOM)
  RoomDetailsOutput getRoom(@Param String roomId, RoomDetailsInput roomDetailsInput);

  @RequestLine(BOOK_ROOM)
  BookRoomInput bookRoom(@Param String roomId, BookRoomInput bookRoomInput);

  @RequestLine(REMOVE_BOOKING)
  RemoveBookingOutput removeBooking(@Param String roomId);

  @RequestLine(REGISTER_VISITORS)
  RegisterVisitorsOutput registerVisitors(RegisterVisitorsInput registerVisitorsInput);

  @RequestLine(SEARCH_VISITORS)
  SearchVisitorsOutput searchVisitors(
      @Param LocalDate startDate, @Param LocalDate EndDate,
      @Param LocalDate birthDate, @Param String firstName, @Param String lastName,
      @Param String phoneNo, @Param LocalDate idCardValidity, @Param String idCardNo,
      @Param LocalDate idCardIssueDate, @Param String roomNo
  );

  @RequestLine(ADD_ROOM)
  AddRoomOutput addRoom(AddRoomInput addRoomInput);

  @RequestLine(UPDATE_ROOM)
  UpdateRoomOutput updateRoom(@Param String roomId, UpdateRoomInput updateRoomInput);

  @RequestLine(PARTIAL_UPDATE_ROOM)
  PartialUpdateRoomOutput partialUpdateRoom(@Param String roomId, PartialUpdateRoomInput partialUpdateRoomInput);

  @RequestLine(DELETE_ROOM)
  DeleteRoomOutput partialUpdateROom(@Param String roomId);
}
