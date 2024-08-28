package com.tinqinacademy.hotel.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.models.input.RoomInput;
import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.input.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.input.RegisterVisitorsInput;
import com.tinqinacademy.hotel.api.operations.updateroom.input.UpdateRoomInput;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.guest.Guest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tinqinacademy.hotel.api.RestApiRoutes.ADD_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.DELETE_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.PARTIAL_UPDATE_ROOM;
import static com.tinqinacademy.hotel.api.RestApiRoutes.REGISTER_VISITORS;
import static com.tinqinacademy.hotel.api.RestApiRoutes.SEARCH_VISITORS;
import static com.tinqinacademy.hotel.api.RestApiRoutes.UPDATE_ROOM;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = HotelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SystemControllerTest extends BaseControllerTest {

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

    Guest guest = Guest.builder()
        .firstName("Jorge")
        .lastName("Russou")
        .birthDate(LocalDate.of(2003, 10, 25))
        .idCardNumber("1234567890")
        .idCardValidity(LocalDate.of(2060, 10, 10))
        .idCardIssueAuthority("MVR RAZGRAD")
        .idCardIssueDate(LocalDate.of(2000, 10, 10))
        .build();

    Booking booking = Booking.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .userId(UUID.fromString("32f16aab-1d57-4d37-a21a-326351a28b8b"))
        .room(savedRoom)
        .guests(List.of(guest))
        .build();
    bookingRepository.save(booking);
  }

  @AfterEach
  void tearDown() {
    bookingRepository.deleteAll();
    guestRepository.deleteAll();
    roomRepository.deleteAll();
  }

  @Test
  void registerVisitors_whenCorrectInput_shouldRespondWithOk() throws Exception {
    Booking booking = bookingRepository.findAll().getFirst();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Dancho")
        .lastName("Ognyanov")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1003002010")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, booking.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());

    Guest guest = guestRepository.findGuestByIdCardNumber(visitorDetailsInput.getIdCardNo()).orElseThrow();
    assertEquals(visitorDetailsInput.getIdCardNo(), guest.getIdCardNumber());
    assertEquals(visitorDetailsInput.getFirstName(), guest.getFirstName());
    assertEquals(visitorDetailsInput.getLastName(), guest.getLastName());
    assertEquals(visitorDetailsInput.getIdCardIssueDate(), guest.getIdCardIssueDate());
    assertEquals(visitorDetailsInput.getIdCardIssueAuthority(), guest.getIdCardIssueAuthority());
    assertEquals(visitorDetailsInput.getIdCardValidity(), guest.getIdCardValidity());
    assertEquals(visitorDetailsInput.getBirthDate(), guest.getBirthDate());
  }

  @Test
  void registerVisitors_whenDuplicateVisitorInInput_shouldRespondWithBadRequestAndErrorResult() throws Exception {
    Booking booking = bookingRepository.findAll().getFirst();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Dancho")
        .lastName("Ognyanov")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1003002010")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput, visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, booking.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].message", is("Visitors must have unique id card numbers")))
        .andExpect(jsonPath("$.statusCode", is("BAD_REQUEST")));

    Optional<Guest> guestMaybe = guestRepository.findGuestByIdCardNumber(visitorDetailsInput.getIdCardNo());
    assertTrue(guestMaybe.isEmpty());
  }

  @Test
  void registerVisitors_whenDatesDontMatchWithBookingDates_shouldRespondWithUnprocessableEntity() throws Exception {
    Booking booking = bookingRepository.findAll().getFirst();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2050, 1, 10))
        .endDate(LocalDate.of(2050, 5, 25))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Dancho")
        .lastName("Ognyanov")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1003002010")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, booking.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Visitor start and end dates must match the booking dates")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Guest> guestMaybe = guestRepository.findGuestByIdCardNumber(visitorDetailsInput.getIdCardNo());
    assertTrue(guestMaybe.isEmpty());
  }

  @Test
  void registerVisitors_whenWrongBookingId_shouldRespondWithNotFoundAndErrorResult() throws Exception {
    String bookingId = UUID.randomUUID().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2050, 10, 18))
        .endDate(LocalDate.of(2050, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Dancho")
        .lastName("Ognyanov")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1003002010")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0].message", is("Entity of type Booking with field id with value " + bookingId + " not found")))
        .andExpect(jsonPath("$.statusCode", is("NOT_FOUND")));

    Optional<Guest> guestMaybe = guestRepository.findGuestByIdCardNumber(visitorDetailsInput.getIdCardNo());
    assertTrue(guestMaybe.isEmpty());
  }

  @Test
  void registerVisitors_whenDuplicateIdCardNo_shouldRespondWithConflictAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Dancho")
        .lastName("Ognyanov")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1234567890")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errors[0].message", is("Guest already registered in booking with id " + bookingId)))
        .andExpect(jsonPath("$.statusCode", is("CONFLICT")));
  }

  @Test
  void registerVisitors_whenMaxBedCapacityReached_shouldRespondWithBadRequestAndErrorResult() throws Exception {
    Room room = roomRepository.findAll().getFirst();
    Bed singleBed = bedRepository.findByBedSize(BedSize.SINGLE).orElseThrow();
    room.setBeds(List.of(singleBed));
    roomRepository.save(room);
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].message", is("Maximum room capacity of 1 reached")))
        .andExpect(jsonPath("$.statusCode", is("BAD_REQUEST")));
  }

  @Test
  void registerVisitors_whenFutureBirthDate_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(3000, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Birth date cannot be a future date")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].birthDate")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenNullBirthDate_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(null)
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Birth date cannot be null")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].birthDate")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenFirstNameTooLong_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan".repeat(11))
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("First name has to be between 2 and 40 characters")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].firstName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenFirstNameTooShort_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("A")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("First name has to be between 2 and 40 characters")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].firstName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenNullFirstName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName(null)
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("First name cannot be blank")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].firstName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenLastNameTooLong_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev".repeat(7))
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Last name has to be between 2 and 40 characters")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].lastName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenLastNameTooShort_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("D")
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Last name has to be between 2 and 40 characters")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].lastName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenNullLastName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName(null)
        .idCardValidity(LocalDate.of(2060, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Last name cannot be blank")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].lastName")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenIdCardValidityIsAPastDate_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2000, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Id card validity cannot be a past date")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].idCardValidity")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenIdCardValidityIsNull_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(null)
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Id card validity cannot be null")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].idCardValidity")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenIdCardIssueDateIsAFutureDate_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2070, 5, 27))
        .idCardIssueDate(LocalDate.of(2060, 5, 27))
        .idCardNo("100232112")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenIdCardNoTooLong_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2030, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1002321121297537")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Id card number must be between 8 and 15 characters")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].idCardNo")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenIdCardNoTooShort_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2030, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1003201")
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Id card number must be between 8 and 15 characters")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].idCardNo")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenNullIdCardNo_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2030, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo(null)
        .idCardIssueAuthority("MVR RAZGRAD")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Id card number cannot be blank")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].idCardNo")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenIdCardIssueAuthorityTooLong_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2030, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1234421122")
        .idCardIssueAuthority("MVR RAZGRAD".repeat(3))
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Id card issue authority must be between 1 and 30 characters")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].idCardIssueAuthority")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenIdCardIssueAuthorityTooShort_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2030, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1234421122")
        .idCardIssueAuthority("")
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void registerVisitors_whenNullIdCardIssueAuthority_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String bookingId = bookingRepository.findAll().getFirst().getId().toString();
    VisitorDetailsInput visitorDetailsInput = VisitorDetailsInput.builder()
        .startDate(LocalDate.of(2025, 10, 18))
        .endDate(LocalDate.of(2025, 10, 26))
        .birthDate(LocalDate.of(2004, 5, 27))
        .firstName("Ivan")
        .lastName("Dobrev")
        .idCardValidity(LocalDate.of(2030, 5, 27))
        .idCardIssueDate(LocalDate.of(2020, 5, 27))
        .idCardNo("1234421122")
        .idCardIssueAuthority(null)
        .build();
    RegisterVisitorsInput input = RegisterVisitorsInput.builder()
        .visitors(List.of(visitorDetailsInput))
        .build();

    mockMvc.perform(post(REGISTER_VISITORS, bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Id card issue authority cannot be blank")))
        .andExpect(jsonPath("$.errors[0].field", is("visitors[0].idCardIssueAuthority")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void searchVisitors_whenStartAndEndDatesNotProvided_shouldRespondWithOkAndListOfAllVisitors() throws Exception {
    mockMvc.perform(get(SEARCH_VISITORS))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.visitors").isNotEmpty())
        .andExpect(jsonPath("$.visitors[0].firstName", is("Jorge")))
        .andExpect(jsonPath("$.visitors[0].lastName", is("Russou")))
        .andExpect(jsonPath("$.visitors[0].idCardNo", is("1234567890")));
  }

  @Test
  void searchVisitors_whenStartAndEndDateDontMatchAnyBooking_shouldRespondWithOkAndEmptyList() throws Exception {
    String startDate = LocalDate.of(2000, 10, 10).toString();
    String endDate = LocalDate.of(2001, 10, 10).toString();

    mockMvc.perform(get(SEARCH_VISITORS)
            .queryParam("startDate", startDate)
            .queryParam("endDate", endDate)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.visitors").isEmpty());

  }

  @Test
  void searchVisitors_whenOnlyStartDateProvided_shouldRespondWithOkAndListOfVisitors() throws Exception {
    String startDate = LocalDate.of(2024, 10, 10).toString();

    mockMvc.perform(get(SEARCH_VISITORS)
            .queryParam("startDate", startDate)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.visitors").isNotEmpty())
        .andExpect(jsonPath("$.visitors[0].firstName", is("Jorge")))
        .andExpect(jsonPath("$.visitors[0].lastName", is("Russou")))
        .andExpect(jsonPath("$.visitors[0].idCardNo", is("1234567890")));
  }

  @Test
  void searchVisitors_whenOnlyEndDateProvided_shouldRespondWithOkAndListOfVisitors() throws Exception {
    String endDate = LocalDate.of(2024, 10, 17).toString();

    mockMvc.perform(get(SEARCH_VISITORS)
            .queryParam("endDate", endDate)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.visitors").isEmpty());
  }

  @Test
  void searchVisitors_whenEndDateIsBeforeStartDate_shouldSwapDatesInternallyAndRespondWithOkAndListOfVisitors() throws Exception {
    String startDate = LocalDate.of(2028, 10, 17).toString();
    String endDate = LocalDate.of(2024, 9, 17).toString();

    mockMvc.perform(get(SEARCH_VISITORS)
            .queryParam("startDate", startDate)
            .queryParam("endDate", endDate)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.visitors").isNotEmpty())
        .andExpect(jsonPath("$.visitors[0].firstName", is("Jorge")))
        .andExpect(jsonPath("$.visitors[0].lastName", is("Russou")))
        .andExpect(jsonPath("$.visitors[0].idCardNo", is("1234567890")));
  }

  @Test
  void addRoom_whenValidInput_shouldRespondWithCreatedAndSaveRoomToTheDb() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("706D")
        .bathroomType("private")
        .floor(7)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(BigDecimal.valueOf(120.64))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.id").exists());

    Room room = roomRepository.findRoomByNumber(roomInput.getRoomNo()).orElseThrow();
    assertEquals(roomInput.getRoomNo(), room.getNumber());
    assertEquals(roomInput.getFloor(), room.getFloor());
    assertEquals(roomInput.getPrice(), room.getPrice());
    assertEquals(roomInput.getBathroomType(), room.getBathroomType().getCode());
    assertEquals(roomInput.getBedSizes().size(), room.getBeds().size());
    assertEquals(roomInput.getBedSizes().stream().sorted().toList(), room.getBeds().stream().map(b -> b.getBedSize().getCode()).sorted().toList());
  }

  @Test
  void addRoom_whenRoomNoIsTaken_shouldRespondWithConflictAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("401A")
        .bathroomType("private")
        .floor(4)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(BigDecimal.valueOf(120.64))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errors[0].message", is("Room with room number " + roomInput.getRoomNo() + " already exists")))
        .andExpect(jsonPath("$.statusCode", is("CONFLICT")));
  }

  @Test
  void addRoom_whenRoomNoTooLong_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("1233231231231223")
        .bathroomType("shared")
        .floor(6)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(BigDecimal.valueOf(120.64))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("RoomNo must be at most 10 characters long")))
        .andExpect(jsonPath("$.errors[0].field", is("roomInput.roomNo")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void addRoom_whenNullRoomNo_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo(null)
        .bathroomType("shared")
        .floor(6)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(BigDecimal.valueOf(120.64))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("RoomNo cannot be null")))
        .andExpect(jsonPath("$.errors[0].field", is("roomInput.roomNo")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void addRoom_whenBlankRoomNo_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("")
        .bathroomType("shared")
        .floor(6)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(BigDecimal.valueOf(120.64))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("RoomNo must be at most 10 characters long")))
        .andExpect(jsonPath("$.errors[0].field", is("roomInput.roomNo")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void addRoom_whenInvalidBathroomType_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("601A")
        .bathroomType("invalidType")
        .floor(6)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(BigDecimal.valueOf(120.64))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Invalid bathroom type")))
        .andExpect(jsonPath("$.errors[0].field", is("roomInput.bathroomType")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void addRoom_whenNullBathroomType_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("601A")
        .bathroomType(null)
        .floor(6)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(BigDecimal.valueOf(120.64))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Bathroom type cannot be null")))
        .andExpect(jsonPath("$.errors[0].field", is("roomInput.bathroomType")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void addRoom_whenNullFloor_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("601A")
        .bathroomType("shared")
        .floor(null)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(BigDecimal.valueOf(120.64))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Floor cannot be null")))
        .andExpect(jsonPath("$.errors[0].field", is("roomInput.floor")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void addRoom_whenNullPrice_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("601A")
        .bathroomType("shared")
        .floor(6)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(null)
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Price cannot be null")))
        .andExpect(jsonPath("$.errors[0].field", is("roomInput.price")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void addRoom_whenNegativePrice_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("601A")
        .bathroomType("shared")
        .floor(6)
        .bedSizes(List.of("kingSize", "double", "double", "kingSize"))
        .price(BigDecimal.valueOf(-100.53))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Price has to be a positive number")))
        .andExpect(jsonPath("$.errors[0].field", is("roomInput.price")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void addRoom_whenNullBedSizes_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("601A")
        .bathroomType("shared")
        .floor(6)
        .bedSizes(null)
        .price(BigDecimal.valueOf(100.53))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("BedSizes cannot be null")))
        .andExpect(jsonPath("$.errors[0].field", is("roomInput.bedSizes")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void addRoom_whenInvalidBedType_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    RoomInput roomInput = RoomInput.builder()
        .roomNo("601A")
        .bathroomType("shared")
        .floor(6)
        .bedSizes(List.of("invalidBedSize"))
        .price(BigDecimal.valueOf(100.53))
        .build();
    AddRoomInput input = AddRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(post(ADD_ROOM)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Invalid bed size")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber(roomInput.getRoomNo());
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void updateRoom_whenValidInput_shouldRespondWithNotFoundAndErrorResult() throws Exception {
    Room roomToUpdate = roomRepository.findRoomByNumber("401A").orElseThrow();
    RoomInput roomInput = RoomInput.builder()
        .roomNo("401B")
        .bathroomType("shared")
        .floor(4)
        .bedSizes(List.of("double", "kingSize"))
        .price(BigDecimal.valueOf(100.53))
        .build();
    UpdateRoomInput input = UpdateRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(put(UPDATE_ROOM, roomToUpdate.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(roomToUpdate.getId().toString())));

    Room room = roomRepository.findRoomByNumber(roomInput.getRoomNo()).orElseThrow();
    assertEquals(roomInput.getRoomNo(), room.getNumber());
    assertEquals(roomInput.getFloor(), room.getFloor());
    assertEquals(roomInput.getPrice(), room.getPrice());
    assertEquals(roomInput.getBathroomType(), room.getBathroomType().getCode());
    assertEquals(roomInput.getBedSizes().size(), room.getBeds().size());
    assertEquals(roomInput.getBedSizes().stream().sorted().toList(), room.getBeds().stream().map(b -> b.getBedSize().getCode()).sorted().toList());
  }

  @Test
  void updateRoom_whenWrongRoomId_shouldRespondWithNotFoundAndErrorResult() throws Exception {
    String wrongRoomId = UUID.randomUUID().toString();
    RoomInput roomInput = RoomInput.builder()
        .roomNo("401B")
        .bathroomType("shared")
        .floor(4)
        .bedSizes(List.of("double", "kingSize"))
        .price(BigDecimal.valueOf(100.53))
        .build();
    UpdateRoomInput input = UpdateRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(put(UPDATE_ROOM, wrongRoomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0].message", is("Entity of type Room with field id with value " + wrongRoomId + " not found")))
        .andExpect(jsonPath("$.statusCode", is("NOT_FOUND")));
  }

  @Test
  void updateRoom_whenInvalidRoomId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String invalidRoomId = "Invalid room id";
    RoomInput roomInput = RoomInput.builder()
        .roomNo("401B")
        .bathroomType("shared")
        .floor(4)
        .bedSizes(List.of("double", "kingSize"))
        .price(BigDecimal.valueOf(100.53))
        .build();
    UpdateRoomInput input = UpdateRoomInput.builder()
        .roomInput(roomInput)
        .build();

    mockMvc.perform(put(UPDATE_ROOM, invalidRoomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("RoomId has to be a valid UUID string")))
        .andExpect(jsonPath("$.errors[0].field", is("roomId")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));
  }

  @Test
  void partialUpdateRoom_whenEmptyInput_shouldRespondWithOk() throws Exception {
    Room oldRoom = roomRepository.findRoomByNumber("401A").orElseThrow();

    PartialUpdateRoomInput input = PartialUpdateRoomInput.builder().build();
    mockMvc.perform(patch(PARTIAL_UPDATE_ROOM, oldRoom.getId().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(oldRoom.getId().toString())));

    Room room = roomRepository.findRoomByNumber(oldRoom.getNumber()).orElseThrow();
    assertEquals(oldRoom.getNumber(), room.getNumber());
    assertEquals(oldRoom.getFloor(), room.getFloor());
    assertEquals(oldRoom.getPrice(), room.getPrice());
    assertEquals(oldRoom.getBathroomType().getCode(), room.getBathroomType().getCode());
    assertEquals(oldRoom.getBeds().size(), room.getBeds().size());
    assertEquals(
        oldRoom.getBeds().stream().map(b -> b.getBedSize().getCode()).sorted().toList(),
        room.getBeds().stream().map(b -> b.getBedSize().getCode()).sorted().toList()
    );
  }

  @Test
  void partialUpdateRoom_whenWrongRoomId_shouldRespondWithNotFoundAndErrorResult() throws Exception {
    String wrongRoomId = UUID.randomUUID().toString();

    PartialUpdateRoomInput input = PartialUpdateRoomInput.builder().build();
    mockMvc.perform(patch(PARTIAL_UPDATE_ROOM, wrongRoomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0].message", is("Entity of type Room with field id with value " + wrongRoomId + " not found")))
        .andExpect(jsonPath("$.statusCode", is("NOT_FOUND")));

  }

  @Test
  void partialUpdateRoom_whenInvalidRoomId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String wrongRoomId = "invalid room id";

    PartialUpdateRoomInput input = PartialUpdateRoomInput.builder().build();
    mockMvc.perform(patch(PARTIAL_UPDATE_ROOM, wrongRoomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("RoomId has to be a valid UUID string")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

  }

  @Test
  void deleteRoom_whenRoomInUser_shouldRespondWithConflictAndErrorOutput() throws Exception {
    Room roomToDelete = roomRepository.findRoomByNumber("401A").orElseThrow();

    mockMvc.perform(delete(DELETE_ROOM, roomToDelete.getId().toString()))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errors[0].message", is("Unable to delete room: Room is still being used")))
        .andExpect(jsonPath("$.statusCode", is("CONFLICT")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber("401A");
    assertTrue(roomMaybe.isPresent());
  }

  @Test
  void deleteRoom_whenCorrectInput_shouldRespondWithOkAndEmptyBody() throws Exception {
    Room roomToDelete = roomRepository.findRoomByNumber("401A").orElseThrow();
    bookingRepository.deleteAll();

    mockMvc.perform(delete(DELETE_ROOM, roomToDelete.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber("401A");
    assertTrue(roomMaybe.isEmpty());
  }

  @Test
  void deleteRoom_whenInvalidRoomId_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
    String invalidRoomId = "Invalid room id";

    mockMvc.perform(delete(DELETE_ROOM, invalidRoomId))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].message", is("Id has to be a valid UUID string")))
        .andExpect(jsonPath("$.errors[0].field", is("id")))
        .andExpect(jsonPath("$.statusCode", is("UNPROCESSABLE_ENTITY")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber("401A");
    assertTrue(roomMaybe.isPresent());
  }

  @Test
  void deleteRoom_whenWrongRoomId_shouldRespondWithNotFoundAndErrorResult() throws Exception {
    String wrongRoomId = UUID.randomUUID().toString();

    mockMvc.perform(delete(DELETE_ROOM, wrongRoomId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0].message", is("Entity of type Room with field id with value " + wrongRoomId + " not found")))
        .andExpect(jsonPath("$.statusCode", is("NOT_FOUND")));

    Optional<Room> roomMaybe = roomRepository.findRoomByNumber("401A");
    assertTrue(roomMaybe.isPresent());
  }
}
