package com.tinqinacademy.hotel.core.mappers;

import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.output.AddRoomOutput;
import com.tinqinacademy.hotel.api.operations.getroom.output.RoomDetailsOutput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.input.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.input.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.output.UpdateRoomOutput;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BedMapper.class, builder = @Builder(disableBuilder = true))
public interface RoomMapper {

  @Mapping(target = ".", source = "roomInput")
  Room fromAddRoomInputToRoom(AddRoomInput input);


  @Mapping(target = ".", source = "roomInput")
  @Mapping(target = "id", source = "id")
  Room fromUpdateRoomInputToRoom(UpdateRoomInput input);

  @Mapping(target = ".", source = "roomInput")
  @Mapping(target = "id", source = "roomId")
  Room fromPartialUpdateRoomInputToRoom(PartialUpdateRoomInput input);

  AddRoomOutput fromRoomToAddRoomOutput(Room room);

  UpdateRoomOutput fromRoomToUpdateRoomOutput(Room room);

  @Mapping(target = "roomOutput", source = "room")
  @Mapping(target = "roomOutput.bedSizes", source = "beds")
  RoomDetailsOutput fromRoomToRoomDetailsOutput(Room room, List<Bed> beds);

}
