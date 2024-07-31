package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.operations.signup.input.SignUpInput;
import com.tinqinacademy.hotel.api.operations.signup.operation.SignUpOperation;
import com.tinqinacademy.hotel.api.operations.signup.output.SignUpOutput;
import com.tinqinacademy.hotel.rest.base.BaseController;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.tinqinacademy.hotel.api.RestApiRoutes.SIGN_UP;

@RestController
@RequiredArgsConstructor
public class UserController extends BaseController {

  private final SignUpOperation signUpOperation;

  @PostMapping(SIGN_UP)
  public ResponseEntity<OperationOutput> signUp(@RequestBody @Valid SignUpInput input) {

    Either<ErrorOutput, SignUpOutput> output = signUpOperation.process(input);

    return consumeEither(output, HttpStatus.CREATED);
  }
}
