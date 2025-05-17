package tk.project.goodsstorage.interaction.crm;

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
@ConditionalOnExpression("'${app.crm-client.type}'.equals('mock')")
public class CrmClientMock implements CrmClient {
    private static final long startInn = 100_000_000_000L;
    private static final long endInn = 1_000_000_000_000L;
    private final Random random;

    public CrmClientMock() {
        this.random = new Random();
    }

    @Async
    @Override
    public CompletableFuture<Map<String, String>> sendRequestInnByLogins(final List<String> logins) {
        log.info("Generate inns in mock for logins : {}", logins);
        return CompletableFuture.completedFuture(
                logins.stream()
                        .collect(Collectors.toMap(Function.identity(), this::getInn))
        );
    }

    private String getInn(final String login) {
        return Long.toString(random.nextLong(startInn, endInn));
    }
}
