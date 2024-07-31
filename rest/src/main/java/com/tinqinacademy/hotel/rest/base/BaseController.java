package com.tinqinacademy.hotel.rest.base;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import io.vavr.control.Either;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

  protected ResponseEntity<OperationOutput> consumeEither(
      Either<? extends ErrorOutput, ? extends OperationOutput> either,
      HttpStatusCode statusCode
  ) {
    return either
        .fold(
            error -> new ResponseEntity<>(error, error.getCode()),
            output -> new ResponseEntity<>(output, statusCode)
        );
  }
}
