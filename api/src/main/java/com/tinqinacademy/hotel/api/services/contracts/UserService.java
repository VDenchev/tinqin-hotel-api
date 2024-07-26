package com.tinqinacademy.hotel.api.services.contracts;

import com.tinqinacademy.hotel.api.operations.signup.input.SignUpInput;
import com.tinqinacademy.hotel.api.operations.signup.output.SignUpOutput;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  SignUpOutput signUp(SignUpInput input);
}
