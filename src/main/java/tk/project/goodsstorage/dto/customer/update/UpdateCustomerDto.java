package tk.project.goodsstorage.dto.customer.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateCustomerDto {

    private final Long id;

    private final String login;

    private final String email;

    private final Boolean isActive;
}