package com.tinqinacademy.hotel.api.operations.partialupdateroom.output;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PartialUpdateRoomOutput implements OperationOutput {

  private String id;
}
