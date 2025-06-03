package startwithco.tosssnapshot.exception.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import startwithco.tosssnapshot.exception.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler({ServerException.class})
    public ResponseEntity<ErrorResponse> handleServerException(final ServerException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus(), exception.getMessage(), exception.getCode()));
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(final BadRequestException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus(), exception.getMessage(), exception.getCode()));
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus(), exception.getMessage(), exception.getCode()));
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(final UnauthorizedException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus(), exception.getMessage(), exception.getCode()));
    }

    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(final ConflictException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus(), exception.getMessage(), exception.getCode()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class ErrorResponse {
        private final int httpStatus;
        private final String message;
        private final String code;
    }
}
