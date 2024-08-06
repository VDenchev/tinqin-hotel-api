package com.tinqinacademy.hotel.api;

public class FeignClientApiRoutes {

  public static final String CHECK_ROOM_AVAILABILITY = "GET " + RestApiRoutes.CHECK_ROOM_AVAILABILITY +
      "?startDate={startDate}&endDate={endDate}&bedCount={bedCount}&bedSizes={bedSizes}&bathroomType={bathroomType}";
  public static final String GET_ROOM = "GET " + RestApiRoutes.GET_ROOM;
  public static final String BOOK_ROOM = "POST " + RestApiRoutes.BOOK_ROOM;
  public static final String REMOVE_BOOKING = "DELETE " + RestApiRoutes.REMOVE_BOOKING;
  public static final String REGISTER_VISITORS = "POST " + RestApiRoutes.REGISTER_VISITORS;
  public static final String SEARCH_VISITORS = "GET " + RestApiRoutes.SEARCH_VISITORS +
      "?startDate={startDate}&endDate={endDate}&birthDate={birthDate}&firstName={firstName}" +
      "&lastName={lastName}&phoneNo={phoneNo}&idCardNo&idCardNo={idCardNo}&idCardValidity={idCardValidity}" +
      "&idCardIssueDate={idCardIssueDate}&roomNo={roomNo}";
  public static final String ADD_ROOM = "POST " + RestApiRoutes.ADD_ROOM;
  public static final String UPDATE_ROOM = "PUT " + RestApiRoutes.UPDATE_ROOM;
  public static final String PARTIAL_UPDATE_ROOM = "PATCH " + RestApiRoutes.PARTIAL_UPDATE_ROOM;
  public static final String DELETE_ROOM = "DELETE " + RestApiRoutes.ROOM_ID;


}
