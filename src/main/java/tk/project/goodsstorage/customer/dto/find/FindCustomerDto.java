package tk.project.goodsstorage.customer.dto.find;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FindCustomerDto {

    Long id;

    String login;

    String email;

    Boolean isActive;
}
