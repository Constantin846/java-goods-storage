package tk.project.goodsstorage.schedulers;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.product.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;
import tk.project.goodsstorage.timer.TaskExecutionTime;
import tk.project.goodsstorage.timer.TaskExecutionTransactionTime;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!local")
@ConditionalOnMissingBean(OptimizedProductPriceScheduler.class)
@ConditionalOnExpression("${app.scheduling.enable:false}")
//@ConditionalOnExpression("${app.scheduling.enable:false} && !${app.scheduling.optimization:false}")
public class SimpleProductPriceScheduler {
    private static final Integer HUNDRED = 100;
    private static final Integer ONE = 1;
    private final ProductRepository productRepository;
    @Value("${app.scheduling.priceIncreasePercentage}")
    private Double priceIncreasePercentage;

    @PostConstruct
    private void postConstruct() {
        log.info("Initialized SIMPLE PRODUCT PRICE SCHEDULER");
    }

    @TaskExecutionTime
    @TaskExecutionTransactionTime
    @Transactional
    @Scheduled(fixedDelayString = "${app.scheduling.period}")
    public void increaseProductPrice() {
        log.info("SIMPLE PRODUCT PRICE SCHEDULER is running");

        double priceIncreasePercentage = ONE + this.priceIncreasePercentage / HUNDRED;
        List<Product> products = productRepository.findAll();
        products = products.stream()
                .map(product -> {
                    product.setPrice(product.getPrice() * priceIncreasePercentage);
                    return product;
                }).toList();
        productRepository.saveAll(products);

        log.info("SIMPLE PRODUCT PRICE SCHEDULER finished");
    }
}
