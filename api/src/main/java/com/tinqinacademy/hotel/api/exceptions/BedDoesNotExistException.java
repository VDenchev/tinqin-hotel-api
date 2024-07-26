package com.tinqinacademy.hotel.api.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BedDoesNotExistException extends RuntimeException{

  private String bedType;

  public BedDoesNotExistException(String bedType) {
    super("Bed of type "+ bedType + " does not exist!");
    this.bedType = bedType;
  }
}
