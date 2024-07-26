package com.tinqinacademy.hotel.core.mappers;

import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.input.RegisterVisitorsInput;
import com.tinqinacademy.hotel.persistence.entities.guest.Guest;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface VisitorMapper {

  @Mapping(target = ".", source = "input")
  Guest fromRegisterVisitorInputToGuest(VisitorDetailsInput input);

//  @Mapping(target = ".", source = "visitors")
  List<Guest> fromRegisterVisitorInputListToGuestList(List<VisitorDetailsInput> visitors);

  default List<Guest> fromRegisterVisitorsInputToGuestList(RegisterVisitorsInput input) {
    return fromRegisterVisitorInputListToGuestList(input.getVisitors());
  }
}
