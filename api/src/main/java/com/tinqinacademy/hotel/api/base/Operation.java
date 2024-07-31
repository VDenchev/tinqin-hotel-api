package com.tinqinacademy.hotel.api.base;

import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import io.vavr.control.Either;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface Operation<I extends OperationInput, O extends OperationOutput> {

  @Transactional
  Either<ErrorOutput, O> process(I input);
}
