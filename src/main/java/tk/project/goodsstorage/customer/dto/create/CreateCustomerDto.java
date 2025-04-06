package tk.project.goodsstorage.customer.dto.create;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCustomerDto {

    String login;

    String email;

    Boolean isActive;
}
