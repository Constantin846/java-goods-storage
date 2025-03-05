package tk.project.goodsstorage.customer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter
public class CustomerIdWrapper {

    private Long customerId;
}
