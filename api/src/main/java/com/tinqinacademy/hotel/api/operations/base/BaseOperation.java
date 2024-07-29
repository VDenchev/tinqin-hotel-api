package com.tinqinacademy.hotel.api.operations.base;

public interface BaseOperation<I, O> {

  O process(I input);
}
