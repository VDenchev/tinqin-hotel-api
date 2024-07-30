package com.tinqinacademy.hotel.api.operations.base;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface BaseOperation<I, O> {

  @Transactional
  O process(I input);
}
