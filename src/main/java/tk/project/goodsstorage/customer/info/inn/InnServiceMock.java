package tk.project.goodsstorage.customer.info.inn;

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
@ConditionalOnExpression("'${app.crm-service.type}'.equals('mock')")
public class InnServiceMock implements InnService {
    private static final long startInn = 100_000_000_000L;
    private static final long endInn = 1_000_000_000_000L;
    private final Random random;

    public InnServiceMock() {
        this.random = new Random();
    }

    @Override
    public CompletableFuture<Map<String, String>> sendRequestInnByLogins(List<String> logins) {
        log.info("Generate inns in mock for logins : {}", logins);
        return CompletableFuture.completedFuture(
                logins.stream()
                        .collect(Collectors.toMap(Function.identity(), this::getInn))
        );
    }

    private String getInn(String login) {
        return Long.toString(random.nextLong(startInn, endInn));
    }
}
