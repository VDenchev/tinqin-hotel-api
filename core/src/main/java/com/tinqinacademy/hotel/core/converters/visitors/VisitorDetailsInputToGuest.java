package com.tinqinacademy.hotel.core.converters.visitors;

import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import com.tinqinacademy.hotel.core.converters.base.BaseConverter;
import com.tinqinacademy.hotel.persistence.entities.guest.Guest;
import org.springframework.stereotype.Component;

@Component
public class VisitorDetailsInputToGuest extends BaseConverter<VisitorDetailsInput, Guest> {

  @Override
  protected Guest doConvert(VisitorDetailsInput source) {
    return Guest.builder()
        .firstName(source.getFirstName())
        .lastName(source.getLastName())
        .birthDate(source.getBirthDate())
        .idCardNumber(source.getIdCardNo())
        .idCardValidity(source.getIdCardValidity())
        .idCardIssueAuthority(source.getIdCardIssueAuthority())
        .idCardIssueDate(source.getIdCardIssueDate())
        .build();
  }
}
