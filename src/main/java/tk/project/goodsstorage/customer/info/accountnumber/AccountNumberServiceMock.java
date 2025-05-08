package tk.project.goodsstorage.customer.info.accountnumber;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnExpression("'${app.account-service.type}'.equals('mock')")
public class AccountNumberServiceMock implements AccountNumberService {
    private static final int startNumber = 10_000_000;
    private static final int endNumber = 100_000_000;
    private final Random random;

    public AccountNumberServiceMock() {
        this.random = new Random();
    }

    @Override
    public CompletableFuture<Map<String, String>> sendRequestAccountNumbersByLogins(final List<String> logins) {
        log.info("Generate account numbers in mock for logins: {}", logins);
        return CompletableFuture.completedFuture(
                logins.stream()
                        .collect(Collectors.toMap(Function.identity(), this::getAccountNumber))
        );
    }

    private String getAccountNumber(final String login) {
        return Integer.toString(random.nextInt(startNumber, endNumber));
    }
}
