package com.tinqinacademy.hotel.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum BathroomType {

  UNKNOWN(""),
  PRIVATE("private"),
  SHARED("shared");

  private final String code;

  BathroomType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator
  public static BathroomType getByCode(String code) {
    return Arrays.stream(BathroomType.values())
        .filter(bt -> bt.getCode().equals(code))
        .findFirst()
        .orElse(UNKNOWN);
  }

  @JsonValue
  @Override
  public String toString() {
    return getCode();
  }
}
