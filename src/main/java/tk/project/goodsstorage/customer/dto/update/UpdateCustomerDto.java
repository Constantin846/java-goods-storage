package tk.project.goodsstorage.customer.dto.update;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCustomerDto {

    Long id;

    String login;

    String email;

    Boolean isActive;
}