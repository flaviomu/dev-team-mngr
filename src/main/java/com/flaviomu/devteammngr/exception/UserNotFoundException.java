package com.flaviomu.devteammngr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Defines an exception to be thrown when a 'Not found' error occurs (404 HTTP Response code)
 *
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found")
public class UserNotFoundException extends RuntimeException{
}
