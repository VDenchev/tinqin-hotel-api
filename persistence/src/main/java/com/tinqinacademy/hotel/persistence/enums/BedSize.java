package com.tinqinacademy.hotel.persistence.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BedSize {

  UNKNOWN("", 0),
  SINGLE("single", 1),
  SMALL_DOUBLE("smallDouble", 2),
  DOUBLE("double", 2),
  QUEEN_SIZE("queenSize", 3),
  KING_SIZE("kingSize", 4);

  BedSize(String code, Integer capacity) {
    this.code = code;
    this.capacity = capacity;
  }

  private final String code;
  private final Integer capacity;

  public static BedSize getByCode(String code) {
    return Arrays.stream(BedSize.values())
        .filter(b -> b.getCode().equals(code))
        .findFirst()
        .orElse(UNKNOWN);
  }

  @Override
  public String toString() {
    return getCode();
  }
}
