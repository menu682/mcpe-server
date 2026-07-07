package ua.pp.mcpe.server.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.pp.mcpe.server.dto.MessageResponseDto;
import ua.pp.mcpe.server.exeptions.BadDataRequestException;
import ua.pp.mcpe.server.exeptions.ConflictException;
import ua.pp.mcpe.server.exeptions.DataNotFoundException;
import ua.pp.mcpe.server.exeptions.ForbiddenException;

/*
Класс отключает генерацию html по умолчанию при возникновении исключений
и если у ответа нет body то отдаёт message
 */

@org.springframework.web.bind.annotation.RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> forbiddenException(Exception ex, WebRequest webRequest) {
        return handleExceptionInternal(ex, null, new HttpHeaders(),
                HttpStatus.FORBIDDEN, webRequest);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> conflictException(Exception ex, WebRequest webRequest) {
        return handleExceptionInternal(ex, null, new HttpHeaders(),
                HttpStatus.CONFLICT, webRequest);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> dataNotFoundException(Exception ex, WebRequest webRequest) {
        return handleExceptionInternal(ex, null, new HttpHeaders(),
                HttpStatus.NOT_FOUND, webRequest);
    }

    @ExceptionHandler(BadDataRequestException.class)
    public ResponseEntity<Object> badDataRequestException(Exception ex, WebRequest webRequest) {
        return handleExceptionInternal(ex, null, new HttpHeaders(),
                HttpStatus.NOT_ACCEPTABLE, webRequest);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherException(Exception ex, WebRequest webRequest) {
        return handleExceptionInternal(ex, null, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        return new ResponseEntity<>((body != null ? body
                : new MessageResponseDto(ex.getMessage())), headers, status);
    }
}

