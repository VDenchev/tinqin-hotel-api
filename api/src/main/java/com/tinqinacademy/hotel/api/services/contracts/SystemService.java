package com.tinqinacademy.hotel.api.services.contracts;

import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.output.AddRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.input.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.output.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.input.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.partialupdateroom.output.PartialUpdateOutput;
import com.tinqinacademy.hotel.api.operations.registervisitors.input.RegisterVisitorsInput;
import com.tinqinacademy.hotel.api.operations.registervisitors.output.RegisterVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.input.SearchVisitorsInput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.output.SearchVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.input.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.output.UpdateRoomOutput;
import org.springframework.stereotype.Service;

@Service
public interface SystemService {

  RegisterVisitorsOutput registerVisitors(RegisterVisitorsInput input);
  SearchVisitorsOutput searchVisitors(SearchVisitorsInput input);
  AddRoomOutput addRoom(AddRoomInput input);
  UpdateRoomOutput updateRoom(UpdateRoomInput input);
  PartialUpdateOutput partialUpdateRoom(PartialUpdateRoomInput input);
  DeleteRoomOutput deleteRoom(DeleteRoomInput input);
}
