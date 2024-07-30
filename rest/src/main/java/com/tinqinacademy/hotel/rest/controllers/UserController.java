package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.operations.base.BaseOperation;
import com.tinqinacademy.hotel.api.operations.signup.input.SignUpInput;
import com.tinqinacademy.hotel.api.operations.signup.output.SignUpOutput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.tinqinacademy.hotel.api.RestApiRoutes.SIGN_UP;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final BaseOperation<SignUpInput, SignUpOutput> signUpOperation;

  @PostMapping(SIGN_UP)
  public ResponseEntity<SignUpOutput> signUp(@RequestBody @Valid SignUpInput input) {

    SignUpOutput output = signUpOperation.process(input);

    return ResponseEntity.ok(output);
  }
}
