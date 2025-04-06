package tk.project.goodsstorage.customer.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCustomerRequest {

    @NotBlank(message = "Customer login must be set")
    @Length(message = "Customer login length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String login;

    @NotBlank(message = "Customer email must be set")
    @Email(message = "Customer email has a wrong format")
    @Length(message = "Customer email length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String email;

    Boolean isActive = true;
}
