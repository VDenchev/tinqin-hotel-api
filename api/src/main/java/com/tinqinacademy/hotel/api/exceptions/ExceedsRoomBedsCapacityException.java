package com.tinqinacademy.hotel.api.exceptions;

public class ExceedsRoomBedsCapacityException extends Exception{
  public ExceedsRoomBedsCapacityException(String message) {
    super(message);
  }
}
