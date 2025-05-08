package tk.project.goodsstorage.dto.customer.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateCustomerRequest {

    @NotBlank(message = "Customer login must be set")
    @Length(message = "Customer login length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String login;

    @NotBlank(message = "Customer email must be set")
    @Email(message = "Customer email has a wrong format")
    @Length(message = "Customer email length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String email;

    private final Boolean isActive;

    private static final boolean IS_ACTIVE_DEFAULT = true;

    public CreateCustomerRequest(String login, String email, Boolean isActive) {
        this.login = login;
        this.email = email;
        this.isActive = Objects.isNull(isActive) ? IS_ACTIVE_DEFAULT : isActive;
    }
}
