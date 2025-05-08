package tk.project.goodsstorage.customer.dto.update;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import tk.project.goodsstorage.validation.NullOrNotBlank;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateCustomerRequest {

    @NullOrNotBlank(message = "Customer login must be null or not blank")
    @Length(message = "Customer login length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String login;

    @NullOrNotBlank(message = "Customer email must be null or not blank")
    @Email(message = "Customer email has a wrong format")
    @Length(message = "Customer email length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    private final String email;

    private final Boolean isActive;
}
