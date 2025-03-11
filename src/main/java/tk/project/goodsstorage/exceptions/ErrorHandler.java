package tk.project.goodsstorage.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tk.project.goodsstorage.exceptions.customer.CustomerNotFoundException;
import tk.project.goodsstorage.exceptions.customer.LoginExistsException;
import tk.project.goodsstorage.exceptions.customer.RequestFindAccountNumberException;
import tk.project.goodsstorage.exceptions.customer.RequestFindInnException;
import tk.project.goodsstorage.exceptions.order.OrderNotAccessException;
import tk.project.goodsstorage.exceptions.order.OrderNotFoundException;
import tk.project.goodsstorage.exceptions.order.OrderStatusAlreadyCancelledException;
import tk.project.goodsstorage.exceptions.order.OrderStatusAlreadyRejectedException;
import tk.project.goodsstorage.exceptions.order.OrderStatusNotCreateException;
import tk.project.goodsstorage.exceptions.product.ArticleExistsException;
import tk.project.goodsstorage.exceptions.product.OperationNotDefinedByStringException;
import tk.project.goodsstorage.exceptions.product.ProductCountNotEnoughException;
import tk.project.goodsstorage.exceptions.product.ProductNotAvailableException;
import tk.project.goodsstorage.exceptions.product.ProductNotFoundException;
import tk.project.goodsstorage.exceptions.product.ProductSpecificationException;
import tk.project.goodsstorage.exceptions.product.ProductsNotFoundByIdsException;

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

        return createApiError(e, message);
    }

    @ExceptionHandler(LoginExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handlerLoginExistsException(final LoginExistsException e) {
        String message = Optional.ofNullable(e.getExistedCustomerId())
                .map(id -> e.getMessage() + " And customer id: " + id)
                .orElseGet(e::getMessage);

        return createApiError(e, message);
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

        return createApiError(e, message);
    }

    @ExceptionHandler(OperationNotDefinedByStringException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handlerOperationNotDefinedBySymbolException(final OperationNotDefinedByStringException e) {
        return createApiError(e);
    }

    @ExceptionHandler(ProductSpecificationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handlerProductSpecificationException(final ProductSpecificationException e) {
        return createApiError(e);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handlerProductNotFoundException(final ProductNotFoundException e) {
        return createApiError(e);
    }

    @ExceptionHandler(ProductsNotFoundByIdsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handlerProductsNotFoundByIdsException(final ProductsNotFoundByIdsException e) {
        return createApiError(e);
    }

    @ExceptionHandler(ProductCountNotEnoughException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handlerProductNotEnoughException(final ProductCountNotEnoughException e) {
        return createApiError(e);
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handlerProductNotAvailableException(final ProductNotAvailableException e) {
        return createApiError(e);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handlerOrderNotFoundException(final OrderNotFoundException e) {
        return createApiError(e);
    }

    @ExceptionHandler(OrderStatusNotCreateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handlerOrderStatusNotCreateException(final OrderStatusNotCreateException e) {
        return createApiError(e);
    }

    @ExceptionHandler(OrderStatusAlreadyCancelledException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handlerOrderStatusAlreadyCancelledException(final OrderStatusAlreadyCancelledException e) {
        return createApiError(e);
    }

    @ExceptionHandler(OrderStatusAlreadyRejectedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handlerOrderStatusAlreadyRejectedException(final OrderStatusAlreadyRejectedException e) {
        return createApiError(e);
    }

    @ExceptionHandler(OrderNotAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiError> handlerOrderNotAccessException(final OrderNotAccessException e) {
        return createApiError(e);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handlerCustomerNotFoundException(final CustomerNotFoundException e) {
        return createApiError(e);
    }

    @ExceptionHandler(RequestFindAccountNumberException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handlerRequestFindAccountNumberException(final RequestFindAccountNumberException e) {
        String message = String.join(DELIMITER, e.getMessage(), e.getReasonException().getMessage());
        return createApiError(e, message);
    }

    @ExceptionHandler(RequestFindInnException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handlerRequestFindInnException(final RequestFindInnException e) {
        String message = String.join(DELIMITER, e.getMessage(), e.getReasonException().getMessage());
        return createApiError(e, message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handlerThrowable(final Throwable e) {
        String message = INTERNAL_SERVER_ERROR;
        log.warn(message, e);
        return createApiError(e, message);
    }

    private ResponseEntity<ApiError> createApiError(final Throwable e) {
        return createApiError(e, e.getMessage());
    }

    private ResponseEntity<ApiError> createApiError(final Throwable e, final String message) {
        ApiError apiError = new ApiError(e.getClass().getSimpleName(), message,
                Instant.now(), e.getStackTrace()[ZERO].getFileName());
        return ResponseEntity.ofNullable(apiError);
    }
}