package com.tinqinacademy.hotel.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BedType {

  UNKNOWN("", 0),
  SINGLE("single", 1),
  SMALL_DOUBLE("smallDouble", 2),
  DOUBLE("double", 2),
  QUEEN_SIZE("queenSize", 3),
  KING_SIZE("kingSize", 4);

  BedType(String code, Integer capacity) {
    this.code = code;
    this.capacity = capacity;
  }

  private final String code;
  private final Integer capacity;

  @JsonCreator
  public static BedType getByCode(String code) {
    return Arrays.stream(BedType.values())
        .filter(b -> b.getCode().equals(code))
        .findFirst()
        .orElse(UNKNOWN);
  }

  @JsonValue
  @Override
  public String toString() {
    return getCode();
  }
}
