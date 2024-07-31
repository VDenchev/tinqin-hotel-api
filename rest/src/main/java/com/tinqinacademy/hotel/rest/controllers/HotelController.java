package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.enums.BathroomType;
import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.bookroom.input.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.operation.BookRoomOperation;
import com.tinqinacademy.hotel.api.operations.bookroom.output.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.input.AvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.operation.CheckAvailableRoomsOperation;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.output.AvailableRoomsOutput;
import com.tinqinacademy.hotel.api.operations.getroom.input.RoomDetailsInput;
import com.tinqinacademy.hotel.api.operations.getroom.operation.GetRoomOperation;
import com.tinqinacademy.hotel.api.operations.getroom.output.RoomDetailsOutput;
import com.tinqinacademy.hotel.api.operations.removebooking.input.RemoveBookingInput;
import com.tinqinacademy.hotel.api.operations.removebooking.operation.RemoveBookingOperation;
import com.tinqinacademy.hotel.api.operations.removebooking.output.RemoveBookingOutput;
import com.tinqinacademy.hotel.rest.base.BaseController;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.tinqinacademy.hotel.api.RestApiRoutes.BOOK_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.CHECK_ROOM_AVAILABILITY;
import static com.tinqinacademy.hotel.api.RestApiRoutes.GET_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.REMOVE_BOOKING;

@RestController
@RequiredArgsConstructor
public class HotelController extends BaseController {

  private final BookRoomOperation bookRoomOperation;
  private final CheckAvailableRoomsOperation checkAvailableRoomsOperation;
  private final GetRoomOperation getRoomOperation;
  private final RemoveBookingOperation removeBookingOperation;

  @io.swagger.v3.oas.annotations.Operation(
      summary = "Checks if a room is available",
      description = "Checks whether a room is available fro a certain period. Room requirements should come as query parameters in URL."
  )
  @ApiResponses(value = {
      @ApiResponse(description = "Returns the IDs of all available rooms", responseCode = "200"),
  })
  @GetMapping(CHECK_ROOM_AVAILABILITY)
  public ResponseEntity<OperationOutput> checkRoomAvailability(
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate,
      @RequestParam(required = false) Integer bedCount,
      @RequestParam(required = false) List<String> bedSizes,
      @RequestParam(required = false) String bathroomType
  ) {
    if (Objects.isNull(bedSizes)) {
      bedSizes = new ArrayList<>();
    }

    AvailableRoomsInput input = AvailableRoomsInput.builder()
        .startDate(startDate)
        .endDate(endDate)
        .bedCount(bedCount)
        .bedSizes(bedSizes.stream().map(BedType::getByCode).toList())
        .bathroomType(BathroomType.getByCode(bathroomType))
        .build();

    Either<ErrorOutput, AvailableRoomsOutput> output = checkAvailableRoomsOperation.process(input);
    return consumeEither(output, HttpStatus.OK);
  }


  @io.swagger.v3.oas.annotations.Operation(
      summary = "Returns room details",
      description = "Returns basic info for a room with the specified id"
  )
  @ApiResponses(value = {
      @ApiResponse(description = "Room is successfully retrieved", responseCode = "200"),
      @ApiResponse(description = "Room with the provided id does not exist", responseCode = "404"),
  })
  @GetMapping(GET_ROOM)
  public ResponseEntity<OperationOutput> getRoom(
      @PathVariable UUID roomId
  ) {
    Either<ErrorOutput, RoomDetailsOutput> output = getRoomOperation.process(RoomDetailsInput.builder()
        .roomId(roomId)
        .build());
    return consumeEither(output, HttpStatus.OK);
  }


  @io.swagger.v3.oas.annotations.Operation(
      summary = "Books a hotel room",
      description = "Books the room with the corresponding id"
  )
  @ApiResponses(value = {
      @ApiResponse(description = "Room is successfully booked", responseCode = "200"),
      @ApiResponse(description = "Validation error", responseCode = "400"),
      @ApiResponse(description = "You dont have permission", responseCode = "403"),
  })
  @PostMapping(BOOK_ROOM)
  public ResponseEntity<OperationOutput> bookRoom(@PathVariable UUID roomId, @Validated @RequestBody BookRoomInput input) {
    input.setRoomId(roomId);
    Either<ErrorOutput, BookRoomOutput> output = bookRoomOperation.process(input);

    return consumeEither(output, HttpStatus.OK);
  }


  @io.swagger.v3.oas.annotations.Operation(
      summary = "Unbooks a hotel room",
      description = "Unbooks the room a user had already booked"
  )
  @ApiResponses(value = {
      @ApiResponse(description = "Room is successfully unbooked", responseCode = "200"),
      @ApiResponse(description = "No book with the provided id", responseCode = "404"),
  })
  @DeleteMapping(REMOVE_BOOKING)
  public ResponseEntity<OperationOutput> removeBooking(@PathVariable UUID bookingId) {
    RemoveBookingInput input = RemoveBookingInput.builder()
        .bookingId(bookingId)
        .build();
    Either<ErrorOutput, RemoveBookingOutput> output = removeBookingOperation.process(input);

    return consumeEither(output, HttpStatus.OK);
  }
}
