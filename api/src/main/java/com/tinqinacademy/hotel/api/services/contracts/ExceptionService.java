package com.tinqinacademy.hotel.api.services.contracts;

import com.tinqinacademy.hotel.api.models.erroroutput.ErrorOutput;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

@Service
public interface ExceptionService {

  ErrorOutput processException(Exception ex, HttpStatusCode statusCode);
}
