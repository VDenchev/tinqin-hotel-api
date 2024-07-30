package com.tinqinacademy.hotel.api.operations.addroom.operation;

import com.tinqinacademy.hotel.api.operations.addroom.input.AddRoomInput;
import com.tinqinacademy.hotel.api.operations.addroom.output.AddRoomOutput;
import com.tinqinacademy.hotel.api.operations.base.BaseOperation;
import org.springframework.stereotype.Service;

@Service
public interface AddRoomOperation extends BaseOperation<AddRoomInput, AddRoomOutput> {
}
