package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.exceptions.CookedException;
import com.tinqinacademy.hotel.api.exceptions.EntityAlreadyExistsException;
import com.tinqinacademy.hotel.api.exceptions.EntityNotFoundException;
import com.tinqinacademy.hotel.api.exceptions.InvalidUUIDException;
import com.tinqinacademy.hotel.api.exceptions.RoomUnavailableException;
import com.tinqinacademy.hotel.api.exceptions.VisitorDateMismatchException;
import com.tinqinacademy.hotel.api.models.erroroutput.ErrorOutput;
import com.tinqinacademy.hotel.api.services.contracts.ExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final ExceptionService exceptionService;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorOutput> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex
  ) {
    final HttpStatus HTTP_STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    ErrorOutput output = exceptionService.processException(ex, HTTP_STATUS);

    return new ResponseEntity<>(output,HTTP_STATUS);
  }

  @ExceptionHandler(CookedException.class)
  public ResponseEntity<Object> handleTestException(CookedException e) {
    return new ResponseEntity<>("You are cooked " + e.getMessage(), HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorOutput> handleEntityNotFoundException(EntityNotFoundException ex) {
    final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    ErrorOutput output = exceptionService.processException(ex, HTTP_STATUS);
    return new ResponseEntity<>(output, HTTP_STATUS);
  }

  @ExceptionHandler(InvalidUUIDException.class)
  public ResponseEntity<ErrorOutput> handleInvalidUUIDException(InvalidUUIDException ex) {
    final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    ErrorOutput output = exceptionService.processException(ex, HTTP_STATUS);
    return new ResponseEntity<>(output, HTTP_STATUS);
  }

  @ExceptionHandler(EntityAlreadyExistsException.class)
  public ResponseEntity<ErrorOutput> handleInvalidUUIDException(EntityAlreadyExistsException ex) {
    final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    ErrorOutput output = exceptionService.processException(ex, HTTP_STATUS);
    return new ResponseEntity<>(output, HTTP_STATUS);
  }

  @ExceptionHandler(RoomUnavailableException.class)
  public ResponseEntity<ErrorOutput> handleInvalidUUIDException(RoomUnavailableException ex) {
    final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    ErrorOutput output = exceptionService.processException(ex, HTTP_STATUS);
    return new ResponseEntity<>(output, HTTP_STATUS);
  }

  @ExceptionHandler(VisitorDateMismatchException.class)
  public ResponseEntity<ErrorOutput> handleInvalidUUIDException(VisitorDateMismatchException ex) {
    final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    ErrorOutput output = exceptionService.processException(ex, HTTP_STATUS);
    return new ResponseEntity<>(output, HTTP_STATUS);
  }
}
