package tk.project.goodsstorage.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestControllerAdvice(basePackages = "tk.project.goodsstorage")
public class ErrorHandler {
    private static final int ZERO = 0;
    private static final String INTERNAL_SERVER_ERROR = "Internal server error";
    private static final String DELIMITER = "; ";

    @ExceptionHandler(ArticleExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handlerArticleExistsException(final ArticleExistsException e) {
        String message = Optional.ofNullable(e.getExistedProductId())
                .map(id -> e.getMessage() + " And product id: " + id)
                .orElseGet(e::getMessage);

        ApiError apiError = new ApiError(e.getClass().getSimpleName(), message,
                Instant.now(), e.getStackTrace()[ZERO].getFileName());
        return ResponseEntity.ofNullable(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handlerValidException(final MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        Set<String> messages = new HashSet<>();

        for (FieldError fieldError : fieldErrors) {
            String message = fieldError != null ? fieldError.getDefaultMessage() : INTERNAL_SERVER_ERROR;
            message = message != null ? message : INTERNAL_SERVER_ERROR;
            messages.add(message);
        }
        String message = String.join(DELIMITER, messages);

        ApiError apiError = new ApiError(e.getClass().getSimpleName(), message,
                Instant.now(), e.getStackTrace()[ZERO].getFileName());
        return ResponseEntity.ofNullable(apiError);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handlerProductNotFoundException(final ProductNotFoundException e) {
        ApiError apiError = new ApiError(e.getClass().getSimpleName(), e.getMessage(),
                Instant.now(), e.getStackTrace()[ZERO].getFileName());
        return ResponseEntity.ofNullable(apiError);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handlerThrowable(final Throwable e) {
        String message = INTERNAL_SERVER_ERROR;
        log.warn(message, e);
        ApiError apiError = new ApiError(e.getClass().getSimpleName(), message,
                Instant.now(), e.getStackTrace()[ZERO].getFileName());
        return ResponseEntity.ofNullable(apiError);
    }
}