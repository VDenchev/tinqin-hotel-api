package com.tinqinacademy.hotel.rest.controllers;


//@SpringBootTest
//@AutoConfigureMockMvc
//class HotelControllerTest {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  @Test
//  void checkRoomAvailability_whenAllQueryParamsArePresent_shouldRespondWithOkAndReturnAListOfRoomIds() throws Exception {
//    mockMvc.perform(get(CHECK_ROOM_AVAILABILITY)
//            .param("startDate", objectMapper.writeValueAsString(LocalDate.now()))
//            .param("endDate", objectMapper.writeValueAsString(LocalDate.now().plusMonths(1)))
//            .param("bedCount", "2")
//            .param("bedSize", "single")
//            .param("bathroomType", "private")
//        )
//        .andExpect(status().isOk())
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$.roomIds[0]", is("13")))
//        .andExpect(jsonPath("$.roomIds[1]", is("18")))
//        .andExpect(jsonPath("$.roomIds[2]", is("27")))
//        .andExpect(jsonPath("$.roomIds[3]", is("40")))
//        .andExpect(jsonPath("$.roomIds[4]", is("41")));
//  }
//
//  @Test
//  void checkRoomAvailability_whenNoQueryParamsArePresent_shouldRespondWithOkAndReturnAListOfRoomIds() throws Exception {
//    mockMvc.perform(get(CHECK_ROOM_AVAILABILITY))
//        .andExpect(status().isOk())
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$.roomIds[0]", is("13")))
//        .andExpect(jsonPath("$.roomIds[1]", is("18")))
//        .andExpect(jsonPath("$.roomIds[2]", is("27")))
//        .andExpect(jsonPath("$.roomIds[3]", is("40")))
//        .andExpect(jsonPath("$.roomIds[4]", is("41")));
//  }
//
//  @Test
//  void getRoom_whenCorrectId_shouldRespondWithOkAndReturnRoomDetails() throws Exception {
//    final UUID ID = UUID.randomUUID();
//
//    mockMvc.perform(get(GET_ROOM, ID))
//        .andExpect(status().isOk())
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$.id", is(ID)))
//        .andExpect(jsonPath("$.number", is("409A")))
//        .andExpect(jsonPath("$.floor", is(25)))
//        .andExpect(jsonPath("$.price", is(10470)))
//        .andExpect(jsonPath("$.bathroomType", is("private")))
//        .andExpect(jsonPath("$.beds[0]", is("kingSize")))
//        .andExpect(jsonPath("$.beds[1]", is("single")))
//        .andExpect(jsonPath("$.datesOccupied[0]", is("09-04-2024")))
//        .andExpect(jsonPath("$.datesOccupied[1]", is("10-04-2024")))
//        .andExpect(jsonPath("$.datesOccupied[2]", is("11-04-2024")));
//  }
//
//  @Test
//  void bookRoom_whenCorrectInput_shouldRespondWithOkAndEmptyResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .endDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("John")
//        .lastName("Doe")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(status().isOk())
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$").isEmpty());
//  }
//
//  @Test
//  void bookRoom_whenInvalidStartDate_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2023, Month.AUGUST, 14))
//        .endDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("John")
//        .lastName("Doe")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenNullStartDate_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .endDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("John")
//        .lastName("Doe")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenInvalidEndDate_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .endDate(LocalDate.of(2023, Month.AUGUST, 19))
//        .firstName("John")
//        .lastName("Doe")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenNullEndDate_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .firstName("John")
//        .lastName("Doe")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenFirstNameTooShort_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .startDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("J")
//        .lastName("Doe")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenFirstNameTooLong_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .startDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("John".repeat(15))
//        .lastName("Doe")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenNullFirstName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .startDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .lastName("Doe")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenBlankFirstName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .startDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("  ")
//        .lastName("Doe")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenLastNameTooShort_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .startDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("John")
//        .lastName("D")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenLastNameTooLong_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .startDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("John")
//        .lastName("Doe".repeat(20))
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenNullLastName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .startDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("John")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void bookRoom_whenBlankLastName_shouldRespondWithUnprocessableEntityAndErrorResult() throws Exception {
//    BookRoomInput input = BookRoomInput.builder()
//        .startDate(LocalDate.of(2025, Month.AUGUST, 14))
//        .startDate(LocalDate.of(2025, Month.AUGUST, 19))
//        .firstName("John")
//        .lastName("  ")
//        .build();
//    mockMvc.perform(post(BOOK_ROOM, "100")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(input))
//        )
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  void removeBooking_whenCorrectBookingId_shouldRespondWithOkAndEmptyResult() throws Exception {
//    mockMvc.perform(delete(REMOVE_BOOKING, "101"))
//        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$").isEmpty());
//  }
//}