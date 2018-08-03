package com.flaviomu.devteammngr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Request and/or BodyRequest not correctly formatted")
public class BadRequestException extends RuntimeException {
}
