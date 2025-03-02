package tk.project.goodsstorage.customer.dto.update;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import tk.project.goodsstorage.validation.NullOrNotBlank;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCustomerRequest {

    @NullOrNotBlank(message = "Customer login must be null or not blank")
    @Length(message = "Customer login length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String login;

    @NullOrNotBlank(message = "Customer email must be null or not blank")
    @Email(message = "Customer email has a wrong format")
    @Length(message = "Customer email length must be between 1 and 64 characters inclusive", min = 1, max = 64)
    String email;

    Boolean isActive;
}
