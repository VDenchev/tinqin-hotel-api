package com.tinqinacademy.hotel.api.operations.getroombyroomno.input;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GetRoomByRoomNoInput implements OperationInput {

  @NotBlank(message = "Room no must not be blank")
  @Size(
      message = "Room number should be at most 10 characters long",
      min = 1, max = 10
  )
  private String roomNo;
}
