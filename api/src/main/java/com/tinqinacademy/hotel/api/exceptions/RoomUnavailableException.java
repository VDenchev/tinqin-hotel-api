package com.tinqinacademy.hotel.api.exceptions;

public class RoomUnavailableException extends RuntimeException {
  public RoomUnavailableException(String message) {
    super(message);
  }
}
