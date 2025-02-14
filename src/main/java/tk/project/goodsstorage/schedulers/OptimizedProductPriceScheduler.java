package tk.project.goodsstorage.schedulers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.timer.TaskExecutionTime;
import tk.project.goodsstorage.timer.TaskExecutionTransactionTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!local")
@ConditionalOnExpression("${app.scheduling.enable:false} && ${app.scheduling.optimization:false}")
public class OptimizedProductPriceScheduler {
    private static final Integer COUNT_ITERATION_PRODUCT = 100_000;
    private static final Integer HUNDRED = 100;
    private static final Integer ONE = 1;
    private final OptimizedProductPriceSchedulerDelegate optimizedProductPriceSchedulerDelegate;
    @Value("${app.scheduling.priceIncreasePercentage}")
    private Double priceIncreasePercentage;

    @PostConstruct
    private void postConstruct() {
        log.info("Initialized OPTIMIZED PRODUCT PRICE SCHEDULER");
    }

    @TaskExecutionTime
    @TaskExecutionTransactionTime
    @Scheduled(fixedDelayString = "${app.scheduling.period}")
    public void increaseProductPrice() {
        log.info("OPTIMIZED PRODUCT PRICE SCHEDULER is running");

        double priceIncreasePercentage = ONE + this.priceIncreasePercentage / HUNDRED;

        int iterator = 0;
        while (optimizedProductPriceSchedulerDelegate
                .increaseProductPrice(iterator, COUNT_ITERATION_PRODUCT, priceIncreasePercentage)) {
            iterator++;
            log.info("Update products: " + iterator * COUNT_ITERATION_PRODUCT);
        }

        log.info("OPTIMIZED PRODUCT PRICE SCHEDULER finished");
    }
}
