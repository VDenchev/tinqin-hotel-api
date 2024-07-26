package com.tinqinacademy.hotel.api.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class EntityNotFoundException extends RuntimeException {

  private String entityName;
  private UUID uuid;

  public EntityNotFoundException(String entityName, UUID uuid) {
    super("Entity of type " + entityName + " and ID " + uuid + " not found!");
    this.entityName = entityName;
    this.uuid = uuid;
  }
}
