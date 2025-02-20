package tk.project.goodsstorage.db;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.product.Product;
import tk.project.goodsstorage.product.model.CategoryType;
import tk.project.goodsstorage.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Deprecated
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!local")
@ConditionalOnProperty(name = "app.dbInit", havingValue = "true", matchIfMissing = false)
public class DbInit {
    private static final String NAME = "name";
    private static final String DESCRIPTION = "des";
    private static final BigDecimal PRICE = BigDecimal.valueOf(100.0);
    private static final Long COUNT = 10L;
    private static final Instant LAST_COUNT_UPDATE_TIME = Instant.now();
    private static final LocalDate CREATE_DATE = LocalDate.now();
    private static final int PRODUCT_COUNT = 1_000_000;
    private static final int TEN_ITERATION = 10;

    private final ProductRepository productRepository;

    @PostConstruct
    private void postConstruct() {
        log.info("DbInit starts generating");
        generateProducts();
        log.info("DbInit finished generating");
    }

    private void generateProducts() {
        int count = PRODUCT_COUNT / TEN_ITERATION;
        Deque<String> articles = generateArticle(count * TEN_ITERATION);

        for (int i = 0; i < TEN_ITERATION; i++) {
            List<Product> products = new ArrayList<>();

            for (int j = 0; j < count; j++) {
                Product product = new Product();
                product.setName(NAME);
                product.setArticle(articles.pop());
                product.setDescription(DESCRIPTION);
                product.setCategory(CategoryType.UNDEFINED);
                product.setPrice(PRICE);
                product.setCount(COUNT);
                product.setLastCountUpdateTime(LAST_COUNT_UPDATE_TIME);
                product.setCreateDate(CREATE_DATE);
                products.add(product);
            }
            productRepository.saveAll(products);
            System.out.println("Added products to db:" + ((i + 1) * count));
        }
    }

    private Deque<String> generateArticle(int count) {
        Set<UUID> uuids = new HashSet<>();
        for (int i = 0; i < count; i++) {
            UUID uuid = UUID.randomUUID();
            if (uuids.contains(uuid)) {
                i--;
            } else {
                uuids.add(uuid);
            }
        }
        return uuids.stream()
                .map(UUID::toString)
                .collect(Collectors.toCollection(ArrayDeque::new));
    }
}
