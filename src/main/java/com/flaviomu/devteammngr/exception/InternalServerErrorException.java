package com.flaviomu.devteammngr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Defines an exception to be thrown when a 'Internal Server Error' error occurs (500 HTTP Response code)
 *
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An unexpected error occurred")
public class InternalServerErrorException extends RuntimeException {
}
