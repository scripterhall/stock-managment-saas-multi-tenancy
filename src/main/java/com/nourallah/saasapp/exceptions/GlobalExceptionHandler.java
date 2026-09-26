package com.nourallah.saasapp.exceptions;

import com.nourallah.saasapp.exceptions.responses.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final BusinessException e,
                                                                       final HttpServletRequest request) {
        log.error("Entity not found", e);
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        HttpStatus status = getHttpStatus(e);
        return ResponseEntity.status(status).body(errorResponse);
    }


    @ExceptionHandler(value = {EntityNotFoundException.class , UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final EntityNotFoundException e,
                                                                       final HttpServletRequest request) {
        log.error("Entity not found", e);
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("NOT_FOUND")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }


    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final BadCredentialsException e,
                                                                       final HttpServletRequest request) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Login or password incorrect")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final MethodArgumentNotValidException e,
                                                                       final HttpServletRequest request) {

        final List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            final String fieldName = ((FieldError)error).getField();
            final String errorCode = error.getDefaultMessage();
            final String defaultMessage = error.getDefaultMessage(); // TODO: add translation later
            errors.add(new ErrorResponse.ValidationError(fieldName, errorCode, defaultMessage));
        });
        log.error("Entity not found", e);
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private HttpStatus getHttpStatus(BusinessException e) {
        if(e instanceof DuplicateResourceException) {
            return HttpStatus.CONFLICT;
        }else if(e instanceof  UnauthorizedException){
            return HttpStatus.UNAUTHORIZED;
        } else if (e instanceof TenantProvioningException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else if(e instanceof InvalidRequestException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
