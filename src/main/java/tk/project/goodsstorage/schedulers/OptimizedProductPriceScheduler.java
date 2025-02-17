package tk.project.goodsstorage.schedulers;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.exceptions.OptimizedProductPriceSchedulingResultWriteFileException;
import tk.project.goodsstorage.product.Product;
import tk.project.goodsstorage.timer.TaskExecutionTime;
import tk.project.goodsstorage.timer.TaskExecutionTransactionTime;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
//@Profile("!local")
@ConditionalOnExpression("${app.scheduling.enable:false} && ${app.scheduling.optimization.enable:false}")
public class OptimizedProductPriceScheduler {
    private final String filePath = this.getClass().getClassLoader()
            .getResource("optimized-product-price-scheduling/result.txt").getPath();
    private static final String DELIMITER = "\n";
    private static final Boolean IS_REWRITING = false;
    private static final Integer COUNT_ITERATION_PRODUCT = 100_000;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final String UPDATE_PRODUCTS_PRICE = """
            UPDATE products p
            SET price = ?
            WHERE p.id = ?
            """;
    private static final String SELECT_PRODUCTS_LIMIT_OFFSET = """
            SELECT *
            FROM products
            LIMIT ?
            OFFSET ?
            """;
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Product> productRowMapper = new RowMapper<Product>() {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(UUID.fromString(rs.getString("id")));
            product.setName(rs.getString("name"));
            product.setArticle(rs.getString("article"));
            product.setDescription(rs.getString("description"));
            product.setCategory(rs.getString("category"));
            product.setPrice(rs.getBigDecimal("price"));
            product.setCount(rs.getLong("count"));
            product.setLastCountUpdateTime(
                    Instant.ofEpochMilli(rs.getTimestamp("last_count_update_time").getTime()));
            product.setCreateDate(LocalDate.parse(rs.getString("create_date")));
            return product;
        }
    };
    @Value("${app.scheduling.priceIncreasePercentage}")
    private BigDecimal priceIncreasePercentage;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;


    @PostConstruct
    private void postConstruct() {
        log.info("Initialized OPTIMIZED PRODUCT PRICE SCHEDULER");
    }

    @TaskExecutionTime
    @TaskExecutionTransactionTime
    @Transactional
    @Scheduled(fixedDelayString = "${app.scheduling.period}")
    public void increaseProductPrice() {
        log.info("OPTIMIZED PRODUCT PRICE SCHEDULER is running");

        BigDecimal priceIncreaseRate = calculatePriceIncreaseRate();

        try (FileWriter fileWriter = new FileWriter(filePath, !IS_REWRITING)) {
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                conn.setAutoCommit(false);
                PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_PRODUCTS_PRICE);

                for (int pageNumber = 0; ; pageNumber++) {
                    int offset = pageNumber * COUNT_ITERATION_PRODUCT;
                    List<Product> products = jdbcTemplate.query(SELECT_PRODUCTS_LIMIT_OFFSET, productRowMapper,
                            COUNT_ITERATION_PRODUCT, offset);
                    if (products.isEmpty()) break;

                    products = products.stream().map(product -> {
                        product.setPrice(product.getPrice().multiply(priceIncreaseRate));
                        return product;
                    }).toList();

                    for (Product product : products) {
                        preparedStatement.setBigDecimal(1, product.getPrice());
                        preparedStatement.setObject(2, product.getId());
                        preparedStatement.executeUpdate();
                    }

                    List<String> productStrings = products.stream().map(this::productToString).toList();
                    String toWrite = String.join(DELIMITER, productStrings);

                    fileWriter.append(toWrite);
                    fileWriter.append(DELIMITER);

                    log.info("Updated products: " + (offset + COUNT_ITERATION_PRODUCT));
                }
                conn.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e); // todo
            }
        } catch (IOException e) {
            throw new OptimizedProductPriceSchedulingResultWriteFileException(e.getMessage());
        }

        log.info("OPTIMIZED PRODUCT PRICE SCHEDULER finished");
    }

    private String productToString(Product product) {
        return product.toString(); // todo
    }

    private BigDecimal calculatePriceIncreaseRate() {
        return this.priceIncreasePercentage.divide(HUNDRED).add(ONE);
    }
}
