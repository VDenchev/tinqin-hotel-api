package com.tinqinacademy.hotel.api.base;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface Operation<I extends OperationInput, O extends OperationOutput> {

  @Transactional
  O process(I input);
}
