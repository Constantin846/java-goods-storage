package tk.project.goodsstorage.customer.dto.find;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerInfo {

    Long id;

    String email;

    String accountNumber;

    String inn;
}
