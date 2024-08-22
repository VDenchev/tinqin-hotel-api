package com.tinqinacademy.hotel.api.operations.getroom.input;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.UUID;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class RoomDetailsInput implements OperationInput {

  @UUID(message = "Id has to be a valid UUID string")
  @NotBlank(message = "Room id must not be blank")
  private String roomId;
}
