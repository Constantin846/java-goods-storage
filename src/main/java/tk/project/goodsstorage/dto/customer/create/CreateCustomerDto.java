package tk.project.goodsstorage.dto.customer.create;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateCustomerDto {

    private final String login;

    private final String email;

    private final Boolean isActive;
}
