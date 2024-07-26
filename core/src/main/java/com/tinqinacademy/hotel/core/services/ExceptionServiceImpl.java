package com.tinqinacademy.hotel.core.services;

import com.tinqinacademy.hotel.api.models.erroroutput.ValidationErrorOutput;
import com.tinqinacademy.hotel.api.models.erroroutput.ErrorOutput;
import com.tinqinacademy.hotel.api.services.contracts.ExceptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ExceptionServiceImpl implements ExceptionService {

  @Override
  public ErrorOutput processException(Exception ex, HttpStatusCode statusCode) {
    log.info("Start processException exception: {}", ex.toString());

    ErrorOutput.ErrorOutputBuilder errorOutputBuilder = ErrorOutput.builder()
        .statusCode(statusCode)
        //TODO: replace ex.getMessage with a custom message
        .message(ex.getMessage());

    if (ex instanceof BindException) {
      List<ValidationErrorOutput> errors = new ArrayList<>();
      ((BindException) ex).getBindingResult().getFieldErrors().forEach(err -> {
        errors.add(ValidationErrorOutput.builder()
            .field(err.getField())
            .message(err.getDefaultMessage())
            .build());
      });
      errorOutputBuilder.errors(errors);
    }

    ErrorOutput output = errorOutputBuilder.build();

    log.info("End processException output: {}", output);
    return output;
  }
}
