package com.projectX.backend.Exceptions;

import com.projectX.backend.Payloads.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionalHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e){
        String message = e.getMessage();
        APIResponse res = new APIResponse(message,false);
        return new ResponseEntity<APIResponse>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e){
        String message = e.getMessage();
        APIResponse res = new APIResponse(message, false);
        return new ResponseEntity<APIResponse>(res, HttpStatus.BAD_REQUEST);
    }


}
