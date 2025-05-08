package tk.project.goodsstorage.customer.dto.find;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CustomerInfo {

    private final Long id;

    private final String email;

    private final String accountNumber;

    private final String inn;
}
