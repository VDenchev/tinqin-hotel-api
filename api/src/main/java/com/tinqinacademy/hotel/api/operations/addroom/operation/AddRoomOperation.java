package com.tinqinacademy.hotel.api.operations.addroom.operation;

import com.tinqinacademy.hotel.api.base.Operation;
import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.output.AddRoomOutput;
import org.springframework.stereotype.Service;

@Service
public interface AddRoomOperation extends Operation<AddRoomInput, AddRoomOutput> {
}
