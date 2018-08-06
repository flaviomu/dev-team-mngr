package com.flaviomu.devteammngr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Defines an exception to be thrown when a 'Bad Request' error occurs (400 HTTP Response code)
 *
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Request and/or BodyRequest not correctly formatted")
public class BadRequestException extends RuntimeException {
}
