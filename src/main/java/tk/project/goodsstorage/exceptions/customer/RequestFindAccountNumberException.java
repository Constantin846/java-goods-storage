package tk.project.goodsstorage.exceptions.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFindAccountNumberException extends RuntimeException {
    private Throwable reasonException;

    public RequestFindAccountNumberException(String message, Throwable e) {
        super(message);
        reasonException = e;
    }

    public RequestFindAccountNumberException(String message) {
        super(message);
    }
}
