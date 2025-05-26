package tk.project.goodsstorage.interaction.account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnExpression("'${app.account-client.type}'.equals('mock')")
public class AccountClientMock implements AccountClient {
    private static final int startNumber = 10_000_000;
    private static final int endNumber = 100_000_000;
    private final Random random;

    public AccountClientMock() {
        this.random = new Random();
    }

    @Async
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
