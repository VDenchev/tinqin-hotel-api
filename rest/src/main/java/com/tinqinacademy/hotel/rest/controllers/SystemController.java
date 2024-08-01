package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.operation.AddRoomOperation;
import com.tinqinacademy.hotel.api.operations.addroom.output.AddRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.input.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.operation.DeleteRoomOperation;
import com.tinqinacademy.hotel.api.operations.deleteroom.output.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.input.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.operation.PartialUpdateRoomOperation;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.output.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.registervisitors.input.RegisterVisitorsInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.operation.RegisterVisitorsOperation;
import com.tinqinacademy.hotel.api.operations.registervisitors.output.RegisterVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.input.SearchVisitorsInput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.operation.SearchVisitorsOperation;
import com.tinqinacademy.hotel.api.operations.searchvisitors.output.SearchVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.input.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.operation.UpdateRoomOperation;
import com.tinqinacademy.hotel.api.operations.updateroom.output.UpdateRoomOutput;
import com.tinqinacademy.hotel.rest.base.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.util.UUID;

import static com.tinqinacademy.hotel.api.RestApiRoutes.ADD_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.DELETE_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.PARTIAL_UPDATE_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.REGISTER_VISITORS;
import static com.tinqinacademy.hotel.api.RestApiRoutes.SEARCH_VISITORS;
import static com.tinqinacademy.hotel.api.RestApiRoutes.UPDATE_ROOM;

@RestController
@RequiredArgsConstructor
public class SystemController extends BaseController {

  private final AddRoomOperation addRoomOperation;
  private final DeleteRoomOperation deleteRoomOperation;
  private final PartialUpdateRoomOperation partialUpdateRoomOperation;
  private final RegisterVisitorsOperation registerVisitorsOperation;
  private final SearchVisitorsOperation searchVisitorsOperation;
  private final UpdateRoomOperation updateRoomOperation;


  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(DeleteRoomInput.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String id) throws IllegalArgumentException {
        DeleteRoomInput input = DeleteRoomInput.builder()
            .id(id)
            .build();
        setValue(input);
      }
    });
  }

  @Operation(
      summary = "Registers a visitor as a room renter"
  )
  @ApiResponses(value = {
      @ApiResponse(
          description = "Successful visitor registration",
          responseCode = "200"
      ),
      @ApiResponse(
          description = "Incorrect input data",
          responseCode = "400"
      ),
      @ApiResponse(
          description = "You don't have permission",
          responseCode = "403"
      )
  })
  @PostMapping(REGISTER_VISITORS)
  public ResponseEntity<OperationOutput> registerVisitors(
      @RequestBody RegisterVisitorsInput input,
      @PathVariable String bookingId
  ) {
    input.setBookingId(bookingId);
    Either<ErrorOutput, RegisterVisitorsOutput> output = registerVisitorsOperation.process(input);

    return consumeEither(output, HttpStatus.OK);
  }


  @Operation(
      summary = "A report which returns when a room was occupied and by who"
  )
  @ApiResponses(value = {
      @ApiResponse(
          description = "A list of all rooms and when they were visited",
          responseCode = "200"
      ),
      @ApiResponse(
          description = "You don't have permission",
          responseCode = "403"
      )
  })
  @GetMapping(SEARCH_VISITORS)
  public ResponseEntity<OperationOutput> searchVisitors(
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate,
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) LocalDate birthDate,
      @RequestParam(required = false) String phoneNumber,
      @RequestParam(required = false) LocalDate cardValidity,
      @RequestParam(required = false) String cardIssueAuthority,
      @RequestParam(required = false) LocalDate cardIssueDate,
      @RequestParam(required = false) String idCardNumber,
      @RequestParam(required = false) String roomNumber
  ) {
    VisitorDetailsInput details = VisitorDetailsInput.builder()
        .startDate(startDate)
        .endDate(endDate)
        .firstName(firstName)
        .lastName(lastName)
        .birthDate(birthDate)
        .phoneNo(phoneNumber)
        .idCardValidity(cardValidity)
        .idCardIssueAuthority(cardIssueAuthority)
        .idCardIssueDate(cardIssueDate)
        .idCardNo(idCardNumber)
        .build();
    SearchVisitorsInput input = SearchVisitorsInput.builder()
        .visitorDetailsInput(details)
        .roomNo(roomNumber)
        .build();

    Either<ErrorOutput, SearchVisitorsOutput> output = searchVisitorsOperation.process(input);

    return consumeEither(output, HttpStatus.OK);
  }


  @Operation(
      summary = "Creates a room"
  )
  @ApiResponses(value = {
      @ApiResponse(
          description = "The id of the newly created room",
          responseCode = "201"
      ),
      @ApiResponse(
          description = "You don't have permission",
          responseCode = "403"
      ),
      @ApiResponse(
          description = "Validation error",
          responseCode = "400"
      )
  })
  @PostMapping(ADD_ROOM)
  public ResponseEntity<OperationOutput> addRoom(@RequestBody AddRoomInput input) {
    Either<ErrorOutput, AddRoomOutput> output = addRoomOperation.process(input);

    return consumeEither(output, HttpStatus.CREATED);
  }


  @Operation(
      summary = "Updates room details"
  )
  @ApiResponses(value = {
      @ApiResponse(
          description = "Returns the id of the updated room",
          responseCode = "200"
      ),
      @ApiResponse(
          description = "You don't have permission",
          responseCode = "403"
      ),
      @ApiResponse(
          description = "Validation error",
          responseCode = "400"
      ),
      @ApiResponse(
          description = "Room with the provided id doesn't exist",
          responseCode = "404"
      )
  })
  @PutMapping(UPDATE_ROOM)
  public ResponseEntity<OperationOutput> updateRoom(
      @PathVariable UUID roomId,
      @RequestBody UpdateRoomInput input
  ) {

    input.setRoomId(roomId);
    Either<ErrorOutput, UpdateRoomOutput> output = updateRoomOperation.process(input);

    return consumeEither(output, HttpStatus.OK);
  }


  @Operation(
      summary = "Partially updates room details"
  )
  @ApiResponses(value = {
      @ApiResponse(
          description = "Returns the id of the updated room",
          responseCode = "200"
      ),
      @ApiResponse(
          description = "You don't have permission",
          responseCode = "403"
      ),
      @ApiResponse(
          description = "Validation error",
          responseCode = "400"
      ),
      @ApiResponse(
          description = "Room with the provided id doesn't exist",
          responseCode = "404"
      )
  })
  @PatchMapping(PARTIAL_UPDATE_ROOM)
  public ResponseEntity<OperationOutput> partialUpdateRoom(
      @PathVariable String roomId,
      @RequestBody PartialUpdateRoomInput input
  ) {
    input.setRoomId(roomId);
    Either<ErrorOutput, PartialUpdateRoomOutput> output = partialUpdateRoomOperation.process(input);

    return consumeEither(output, HttpStatus.OK);
  }


  @Operation(
      summary = "Deletes a room with provided id"
  )
  @ApiResponses(value = {
      @ApiResponse(
          description = "An empty response indicating that the deletion was successful",
          responseCode = "200"
      ),
      @ApiResponse(
          description = "Room with the provided id doesn't exist",
          responseCode = "404"
      )
  })
  @DeleteMapping(DELETE_ROOM)
  public ResponseEntity<OperationOutput> deleteRoom(@PathVariable("roomId") DeleteRoomInput input) {

    Either<ErrorOutput, DeleteRoomOutput> output = deleteRoomOperation.process(input);

    return consumeEither(output, HttpStatus.OK);
  }
}
