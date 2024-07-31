package com.tinqinacademy.hotel.api.base;

import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.Validator;

import java.util.List;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
public abstract class BaseOperationProcessor {

  protected final ConversionService conversionService;
  protected final Validator validator;

  protected API.Match.Case<? extends Throwable, ErrorOutput> customStatusCase(Throwable t, Class<? extends Throwable> clazz, HttpStatusCode statusCode) {
    ErrorOutput output = ErrorOutput.builder()
        .code(statusCode)
        .errors(List.of(Error.builder()
            .message(t.getMessage())
            .build()
        ))
        .build();
    return createCase(clazz, output);
  }

  protected API.Match.Case<? extends Throwable, ErrorOutput> defaultCase(Throwable t) {
    ErrorOutput output = ErrorOutput.builder()
        .code(HttpStatus.INTERNAL_SERVER_ERROR)
        .errors(List.of(Error.builder()
            .message(t.getMessage())
            .build()
        ))
        .build();
    return createDefaultCase(output);
  }

  protected API.Match.Case<? extends Throwable, ErrorOutput> createCase(Class<? extends Throwable> clazz, ErrorOutput errorOutput) {
    return Case($(instanceOf(clazz)), errorOutput);
  }

  protected API.Match.Case<? extends Throwable, ErrorOutput> createDefaultCase(ErrorOutput errorOutput) {
    return Case($(), errorOutput);
  }
}
