package tk.project.goodsstorage.customer.dto.find;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class FindCustomerResponse {

    private final Long id;

    private final String login;

    private final String email;

    private final Boolean isActive;
}