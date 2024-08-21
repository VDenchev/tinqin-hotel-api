package com.tinqinacademy.hotel.api.exceptions;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

  private String entityName;
  private String field;
  private String value;

  public EntityNotFoundException(String entityName,String field, String value) {
    super(String.format("Entity of type %s with field %s with value %s not found", entityName, field, value));
    this.entityName = entityName;
    this.field = field;
    this.value = value;
  }
}
