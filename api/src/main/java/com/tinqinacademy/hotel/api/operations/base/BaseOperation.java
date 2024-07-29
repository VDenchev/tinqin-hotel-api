package com.tinqinacademy.hotel.api.operations.base;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface BaseOperation<I, O> {

  O process(I input);
}
