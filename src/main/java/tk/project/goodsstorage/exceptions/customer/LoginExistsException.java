package tk.project.goodsstorage.exceptions.customer;

import lombok.Getter;

@Getter
public class LoginExistsException extends RuntimeException {
    private final Long existedCustomerId;

    public LoginExistsException(String message, Long existedCustomerId) {
        super(message);
        this.existedCustomerId = existedCustomerId;
    }
}
