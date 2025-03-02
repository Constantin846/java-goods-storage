package tk.project.goodsstorage.exceptions.currency;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestGetCurrenciesException extends RuntimeException {
    private Throwable reasonException;

    public RequestGetCurrenciesException(String message, Throwable e) {
        super(message);
        this.reasonException = e;
    }

    public RequestGetCurrenciesException(String message) {
        super(message);
    }
}
