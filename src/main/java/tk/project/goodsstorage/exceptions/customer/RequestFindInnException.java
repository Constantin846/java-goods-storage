package tk.project.goodsstorage.exceptions.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFindInnException extends RuntimeException {
    private Throwable reasonException;

    public RequestFindInnException(String message, Throwable e) {
        super(message);
        reasonException = e;
    }

    public RequestFindInnException(String message) {
        super(message);
    }
}
