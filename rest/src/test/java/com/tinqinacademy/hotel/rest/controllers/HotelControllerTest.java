package com.tinqinacademy.hotel.rest.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.operations.bookroom.input.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.removebooking.input.RemoveBookingInput;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.BookingRepository;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.hotel.rest.HotelApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tinqinacademy.hotel.api.RestApiRoutes.BOOK_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.CHECK_ROOM_AVAILABILITY;
import static com.tinqinacademy.hotel.api.RestApiRoutes.GET_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.GET_ROOM_BY_ROOM_NO;
import static com.tinqinacademy.hotel.api.RestApiRoutes.REMOVE_BOOKING;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = HotelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HotelControllerTest extends BaseControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private BedRepository bedRepository;
  @Autowired
  private RoomRepository roomRepository;
  @Autowired
  private BookingRepository bookingRepository;
  @Autowired
  private GuestRepository guestRepository;

  @BeforeEach
  void setUp() {
    Bed singleBed = bedRepository.findByBedSize(BedSize.SINGLE).orElseThrow();
    Bed doubleBed = bedRepository.findByBedSize(BedSize.DOUBLE).orElseThrow();
    List<Bed> beds = new ArrayList<>();
    beds.add(singleBed);
    beds.add(singleBed);
    beds.add(doubleBed);
    Room room = Room.builder()
        .number("401A")
        .floor(1)
        .price(BigDecimal.valueOf(130.30))
        .beds(beds)
        .bathroomType(BathroomType.PRIVATE)
        .build();
    Room savedRoom = roomRepository.save(room);

    Booking booking = Booking.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .userId(UUID.fromString("32f16aab-1d57-4d37-a21a-326351a28b8b"))
        .room(savedRoom)
        .build();
    bookingRepository.save(booking);
  }

  @AfterEach
  void tearDown() {
    guestRepository.deleteAll();
    bookingRepository.deleteAll();
    roomRepository.deleteAll();
  }

  @Test
  void checkRoomAvailability_whenAllValidParams_shouldRespondWithOkAndReturnAListOfRoomIds() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();

    mockMvc.perform(get(CHECK_ROOM_AVAILABILITY)
            .queryParam("startDate", "2024-08-26")
            .queryParam("endDate", "2024-09-14")
            .queryParam("bedCount", "3")
            .queryParam("bedSizes", "single", "double")
            .queryParam("bathroomType", "private")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.roomIds[0]", is(room.getId().toString())));
  }

  @Test
  void checkRoomAvailability_whenDatesOverlappingOnTheLeft_shouldRespondWithOkAndEmptyList() throws Exception {
    mockMvc.perform(get(CHECK_ROOM_AVAILABILITY)
            .queryParam("startDate", "2025-10-14")
            .queryParam("endDate", "2025-10-18")
            .queryParam("bedCount", "3")
            .queryParam("bedSizes", "single", "double")
            .queryParam("bathroomType", "private")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.roomIds").isEmpty());
  }

  @Test
  void checkRoomAvailability_whenDatesInBetweenBookingDates_shouldRespondWithOkAndEmptyList() throws Exception {
    mockMvc.perform(get(CHECK_ROOM_AVAILABILITY)
            .queryParam("startDate", "2025-10-20")
            .queryParam("endDate", "2025-10-24")
            .queryParam("bedCount", "3")
            .queryParam("bedSizes", "single", "double")
            .queryParam("bathroomType", "private")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.roomIds").isEmpty());
  }

  @Test
  void checkRoomAvailability_whenDatesOverlappingOnTheRight_shouldRespondWithOkAndEmptyList() throws Exception {
    mockMvc.perform(get(CHECK_ROOM_AVAILABILITY)
            .queryParam("startDate", "2025-10-26")
            .queryParam("endDate", "2025-10-29")
            .queryParam("bedCount", "3")
            .queryParam("bedSizes", "single", "double")
            .queryParam("bathroomType", "private")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.roomIds").isEmpty());
  }

  @Test
  void checkRoomAvailability_whenStartDateIsAfterEndDate_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    mockMvc.perform(get(CHECK_ROOM_AVAILABILITY)
            .queryParam("startDate", "2025-07-23")
            .queryParam("endDate", "2024-02-12")
            .queryParam("bedCount", "3")
            .queryParam("bedSizes", "single", "double")
            .queryParam("bathroomType", "private")
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Start date must be before end date")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
    ;
  }

  @Test
  void checkRoomAvailability_whenOnlyStartDateParam_shouldRespondWithOkAndReturnAListOfRoomIds() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();

    mockMvc.perform(get(CHECK_ROOM_AVAILABILITY)
            .queryParam("startDate", "2024-08-26")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.roomIds[0]", is(room.getId().toString())));
  }

  @Test
  void getRoom_whenCorrectRoomId_shouldRespondWithOkAndReturnRoomDetails() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();

    mockMvc.perform(get(GET_ROOM, room.getId().toString())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(room.getId().toString())))
        .andExpect(jsonPath("$.number", is(room.getNumber())))
        .andExpect(jsonPath("$.floor", is(room.getFloor())))
        .andExpect(jsonPath("$.price").value("130.3"))
        .andExpect(jsonPath("$.bathroomType", is(room.getBathroomType().getCode())))
        .andExpect(jsonPath("$.bedSizes[0]", is("single")))
        .andExpect(jsonPath("$.bedSizes[1]", is("single")))
        .andExpect(jsonPath("$.bedSizes[2]", is("double")))
        .andExpect(jsonPath("$.datesOccupied[0]", is("2025-10-18")))
        .andExpect(jsonPath("$.datesOccupied[1]", is("2025-10-19")))
        .andExpect(jsonPath("$.datesOccupied[2]", is("2025-10-20")))
        .andExpect(jsonPath("$.datesOccupied[3]", is("2025-10-21")))
        .andExpect(jsonPath("$.datesOccupied[4]", is("2025-10-22")))
        .andExpect(jsonPath("$.datesOccupied[5]", is("2025-10-23")))
        .andExpect(jsonPath("$.datesOccupied[6]", is("2025-10-24")))
        .andExpect(jsonPath("$.datesOccupied[7]", is("2025-10-25")))
        .andExpect(jsonPath("$.datesOccupied[8]", is("2025-10-26")));
  }

  @Test
  void getRoom_whenWrongRoomId_shouldRespondWithNotFoundAndErrorResult() throws Exception {
    String wrongId = "1325b719-9c8c-4282-9a74-0fcd03bbba52";
    mockMvc.perform(get(GET_ROOM, wrongId)
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0].message", is("Entity of type Room with field id with value " + wrongId + " not found")))
        .andExpect(jsonPath("$.statusCode", is("NOT_FOUND")));
  }

  @Test
  void getRoom_whenInvalidRoomId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String wrongId = "not a valid id";
    mockMvc.perform(get(GET_ROOM, wrongId)
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Room id has to be a valid UUID string")))
        .andExpect(jsonPath("$.errors[0].field", is("roomId")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }


  @Test
  void bookRoom_whenCorrectInput_shouldRespondWithOkAndEmptyResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());

    List<Booking> bookings = bookingRepository.getBookingsOfRoomForPeriod(room.getId(),
        input.getStartDate(), input.getEndDate());
    assertFalse(bookings.isEmpty());
    assertEquals(1, bookings.size());
    Booking booking = bookings.getFirst();
    assertEquals(input.getStartDate(), booking.getStartDate());
    assertEquals(input.getEndDate(), booking.getEndDate());
    assertEquals(input.getUserId(), booking.getUserId().toString());
    assertEquals(roomId, booking.getRoom().getId().toString());
    assertTrue(booking.getGuests().isEmpty());
  }

  @Test
  void bookRoom_whenWrongRoomId_shouldRespondWithNotFoundAndErrorResult() throws Exception {
    String wrongRoomId = "695c0e49-b26b-4c7f-9ec8-2a3cb50c9465";
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
        .endDate(LocalDate.of(2025, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, wrongRoomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0].message", is("Entity of type Room with field id with value " + wrongRoomId + " not found")))
        .andExpect(jsonPath("$.statusCode", is("NOT_FOUND")));

    List<Booking> bookings = bookingRepository.getBookingsOfRoomForPeriod(UUID.fromString(wrongRoomId),
        input.getStartDate(), input.getEndDate());
    assertTrue(bookings.isEmpty());
  }

  @Test
  void bookRoom_whenInvalidRoomId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String invalidRoomId = "invalid room id";
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2049, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, invalidRoomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Room id has to be a valid UUID string")))
        .andExpect(jsonPath("$.errors[0].field", is("roomId")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenDatesDontMatch_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2051, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Start date must be before endDate")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenStartDateInThePast_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2000, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Start date must be a future date")))
        .andExpect(jsonPath("$.errors[0].field", is("startDate")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenEndDateInThePast_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2000, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenInvalidUserId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("invalid user id")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("User id has to be a valid UUID string")))
        .andExpect(jsonPath("$.errors[0].field", is("userId")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenNullUserId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId(null)
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("User id cannot be blank")))
        .andExpect(jsonPath("$.errors[0].field", is("userId")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenInvalidPhoneNoFormat_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("invalid phone no")
        .firstName("John")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Invalid phoneNo format")))
        .andExpect(jsonPath("$.errors[0].field", is("phoneNo")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenFirstNameTooShort_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("J")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("First name must be between 2 and 40 characters long")))
        .andExpect(jsonPath("$.errors[0].field", is("firstName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenFirstNameTooLong_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John".repeat(11))
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("First name must be between 2 and 40 characters long")))
        .andExpect(jsonPath("$.errors[0].field", is("firstName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenBlankFirstName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("")
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenNullFirstName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName(null)
        .lastName("Doe")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("First name cannot be blank")))
        .andExpect(jsonPath("$.errors[0].field", is("firstName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenLastNameTooShort_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("D")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Last name must be between 2 and 40 characters long")))
        .andExpect(jsonPath("$.errors[0].field", is("lastName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenLastNameTooLong_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("Doe".repeat(15))
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Last name must be between 2 and 40 characters long")))
        .andExpect(jsonPath("$.errors[0].field", is("lastName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenBlankLastName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName("")
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void bookRoom_whenNullLastName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Room room = roomRepository.findRoomByNumber("401A").orElseThrow();
    String roomId = room.getId().toString();
    BookRoomInput input = BookRoomInput.builder()
        .startDate(LocalDate.of(2050, Month.AUGUST, 14))
        .endDate(LocalDate.of(2050, Month.AUGUST, 19))
        .userId("01f7970c-a027-4900-82e0-02b42c480def")
        .phoneNo("+359 113654872")
        .firstName("John")
        .lastName(null)
        .build();

    mockMvc.perform(post(BOOK_ROOM, roomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Last name cannot be blank")))
        .andExpect(jsonPath("$.errors[0].field", is("lastName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void removeBooking_whenCorrectBookingIdAndUserId_shouldRespondWithOkAndEmptyResult() throws Exception {
    Booking booking = bookingRepository.findAll().getFirst();
    RemoveBookingInput input = RemoveBookingInput.builder()
        .userId("32f16aab-1d57-4d37-a21a-326351a28b8b")
        .build();

    mockMvc.perform(delete(REMOVE_BOOKING, booking.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());

    List<Booking> bookings = bookingRepository.findAll();
    assertTrue(bookings.isEmpty());
  }

  @Test
  void removeBooking_whenCorrectBookingIdAndWrongUserId_shouldRespondWithNotFoundAndErrorResult() throws Exception {
    Booking booking = bookingRepository.findAll().getFirst();
    RemoveBookingInput input = RemoveBookingInput.builder()
        .userId("16e0f301-d986-4c19-ab82-62c224106692")
        .build();

    mockMvc.perform(delete(REMOVE_BOOKING, booking.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0].message", is("Entity of type Booking with field id with value " + booking.getId() + " not found")))
        .andExpect(jsonPath("$.statusCode", is("NOT_FOUND")));

    List<Booking> bookings = bookingRepository.findAll();
    assertFalse(bookings.isEmpty());
  }

  @Test
  void removeBooking_whenWrongBookingIdAndCorrectUserId_shouldRespondWithNotFoundAndErrorResult() throws Exception {
    String wrongBookingId = UUID.randomUUID().toString();
    RemoveBookingInput input = RemoveBookingInput.builder()
        .userId("32f16aab-1d57-4d37-a21a-326351a28b8b")
        .build();

    mockMvc.perform(delete(REMOVE_BOOKING, wrongBookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0].message", is("Entity of type Booking with field id with value " + wrongBookingId + " not found")))
        .andExpect(jsonPath("$.statusCode", is("NOT_FOUND")));

    List<Booking> bookings = bookingRepository.findAll();
    assertFalse(bookings.isEmpty());
  }

  @Test
  void removeBooking_whenInvalidUserId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Booking booking = bookingRepository.findAll().getFirst();
    RemoveBookingInput input = RemoveBookingInput.builder()
        .userId("not a valid user id")
        .build();

    mockMvc.perform(delete(REMOVE_BOOKING, booking.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("User id has to be a valid UUID string")))
        .andExpect(jsonPath("$.errors[0].field", is("userId")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    List<Booking> bookings = bookingRepository.findAll();
    assertFalse(bookings.isEmpty());
  }

  @Test
  void removeBooking_whenNullUserId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    Booking booking = bookingRepository.findAll().getFirst();
    RemoveBookingInput input = RemoveBookingInput.builder()
        .userId(null)
        .build();

    mockMvc.perform(delete(REMOVE_BOOKING, booking.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("User id cannot be blank")))
        .andExpect(jsonPath("$.errors[0].field", is("userId")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    List<Booking> bookings = bookingRepository.findAll();
    assertFalse(bookings.isEmpty());
  }

  @Test
  void removeBooking_whenInvalidBookingIdAndCorrectUserId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String wrongBookingId = "Invalid booking id";
    RemoveBookingInput input = RemoveBookingInput.builder()
        .userId("32f16aab-1d57-4d37-a21a-326351a28b8b")
        .build();

    mockMvc.perform(delete(REMOVE_BOOKING, wrongBookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Booking id has to be a valid UUID string")))
        .andExpect(jsonPath("$.errors[0].field", is("bookingId")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    List<Booking> bookings = bookingRepository.findAll();
    assertFalse(bookings.isEmpty());
  }

  @Test
  void getRoomByRoomNo_whenValidInput_shouldRespondWithOk() throws Exception {
    String roomNo = "401A";

    mockMvc.perform(get(GET_ROOM_BY_ROOM_NO, roomNo)
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNotEmpty());
  }

  @Test
  void getRoomByRoomNo_whenWrongRoomNo_shouldRespondWithOk() throws Exception {
    String wrongRoomNo = "105B";

    mockMvc.perform(get(GET_ROOM_BY_ROOM_NO, wrongRoomNo)
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].message", is("Entity of type Room with field roomNo with value " + wrongRoomNo + " not found")))
        .andExpect(jsonPath("$.statusCode", is("BAD_REQUEST")));
  }
}