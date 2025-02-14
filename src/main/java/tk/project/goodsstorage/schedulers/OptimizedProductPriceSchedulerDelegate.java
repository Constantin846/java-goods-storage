package tk.project.goodsstorage.schedulers;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.product.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(OptimizedProductPriceScheduler.class)
public class OptimizedProductPriceSchedulerDelegate {
    private final ProductRepository productRepository;

    @PostConstruct
    private void postConstruct() {
        log.info("Initialized OPTIMIZED PRODUCT PRICE SCHEDULER DELEGATE");
    }

    @Transactional
    public boolean increaseProductPrice(int pageNumber, int pageSize, double priceIncreasePercentage) {
        List<Product> products = productRepository
                .findAll(PageRequest.of(pageNumber, pageSize)).stream().toList();
        if (products.isEmpty()) return false;

        products = products.stream()
                .map(product -> {
                    product.setPrice(product.getPrice() * priceIncreasePercentage);
                    return product;
                }).toList();
        productRepository.saveAll(products);
        return true;
    }
}
