package com.tinqinacademy.hotel.api;

public class FeignClientApiRoutes {

  public static final String GET_METHOD = "GET ";
  public static final String POST_METHOD = "POST ";
  public static final String DELETE_METHOD = "DELETE ";
  public static final String PUT_METHOD = "PUT ";
  public static final String PATCH_METHOD = "PATCH ";

  public static final String CHECK_ROOM_AVAILABILITY = GET_METHOD + RestApiRoutes.CHECK_ROOM_AVAILABILITY +
      "?startDate={startDate}" +
      "&endDate={endDate}" +
      "&bedCount={bedCount}" +
      "&bedSizes={bedSizes}" +
      "&bathroomType={bathroomType}";
  public static final String GET_ROOM = GET_METHOD + RestApiRoutes.GET_ROOM;
  public static final String BOOK_ROOM = POST_METHOD + RestApiRoutes.BOOK_ROOM;
  public static final String REMOVE_BOOKING = DELETE_METHOD + RestApiRoutes.REMOVE_BOOKING;
  public static final String REGISTER_VISITORS = POST_METHOD + RestApiRoutes.REGISTER_VISITORS;
  public static final String SEARCH_VISITORS = GET_METHOD + RestApiRoutes.SEARCH_VISITORS +
      "?startDate={startDate}&endDate={endDate}&birthDate={birthDate}" +
      "&firstName={firstName}&lastName={lastName}&phoneNo={phoneNo}" +
      "&idCardNo={idCardNo}&idCardValidity={idCardValidity}" +
      "&idCardIssueDate={idCardIssueDate}&roomNo={roomNo}";
  public static final String ADD_ROOM = POST_METHOD + RestApiRoutes.ADD_ROOM;
  public static final String UPDATE_ROOM = PUT_METHOD + RestApiRoutes.UPDATE_ROOM;
  public static final String PARTIAL_UPDATE_ROOM = PATCH_METHOD + RestApiRoutes.PARTIAL_UPDATE_ROOM;
  public static final String DELETE_ROOM = DELETE_METHOD + RestApiRoutes.DELETE_ROOM;
  public static final String GET_ROOM_BY_ROOM_NO = GET_METHOD + RestApiRoutes.GET_ROOM_BY_ROOM_NO;
}
