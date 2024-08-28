package com.tinqinacademy.hotel.api.operations.partialupdateroom.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.models.input.RoomInput;
import com.tinqinacademy.hotel.api.validation.groups.NonMandatoryFieldsGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PartialUpdateRoomInput implements OperationInput {

  @JsonUnwrapped
  @Valid
  private RoomInput roomInput;

  @JsonIgnore
  @UUID(message = "RoomId has to be a valid UUID string", groups = {Default.class, NonMandatoryFieldsGroup.class})
  @NotBlank(message = "Room id must not be blank", groups = {Default.class, NonMandatoryFieldsGroup.class})
  private String roomId;
}
