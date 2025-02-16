package tk.project.goodsstorage.schedulers;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.exceptions.OptimizedProductPriceSchedulingResultWriteFileException;
import tk.project.goodsstorage.product.Product;
import tk.project.goodsstorage.timer.TaskExecutionTime;
import tk.project.goodsstorage.timer.TaskExecutionTransactionTime;

import java.io.FileWriter;
import java.io.IOException;
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
@ConditionalOnExpression("${app.scheduling.enable:false} && ${app.scheduling.optimization:false}")
public class OptimizedProductPriceScheduler {
    private final String filePath = this.getClass().getClassLoader()
            .getResource("optimized-product-price-scheduling/result.txt").getPath();
/*    File file = new File(this.getClass().getClassLoader()
            .getResource("optimized-product-price-scheduling/result.txt").getFile()); todo delete*/
    private static final String DELIMITER = "\n";
    private static final Boolean IS_REWRITING = false;
    private static final Integer COUNT_ITERATION_PRODUCT = 100_00;
    private static final Integer HUNDRED = 100;
    private static final Integer ONE = 1;
    private static final String UPDATE_PRODUCTS_PRICE = """
            UPDATE products p
            SET price = price * ?
            WHERE p.id IN (SELECT id
                      FROM products
                      LIMIT ?
                      OFFSET ?)
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
            product.setPrice(rs.getDouble("price"));
            product.setCount(rs.getLong("count"));
            product.setLastCountUpdateTime(
                    Instant.ofEpochMilli(rs.getTimestamp("last_count_update_time").getTime()));
            product.setCreateDate(LocalDate.parse(rs.getString("create_date")));
            return product;
        }
    };
    @Value("${app.scheduling.priceIncreasePercentage}")
    private Double priceIncreasePercentage;

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

        double priceIncreaseRate = ONE + this.priceIncreasePercentage / HUNDRED;

        writeResultToFile(priceIncreaseRate);

        log.info("OPTIMIZED PRODUCT PRICE SCHEDULER finished");
    }

    private void writeResultToFile(double priceIncreaseRate) {
        try (FileWriter fileWriter = new FileWriter(filePath, !IS_REWRITING)) {
            for (int pageNumber = 0;; pageNumber++) {
                int offset = pageNumber * COUNT_ITERATION_PRODUCT;
                jdbcTemplate.update(UPDATE_PRODUCTS_PRICE, new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setDouble(1, priceIncreaseRate);
                        ps.setInt(2, COUNT_ITERATION_PRODUCT);
                        ps.setInt(3, offset);
                    }
                });

                List<Product> products = jdbcTemplate.query(SELECT_PRODUCTS_LIMIT_OFFSET, productRowMapper,
                        COUNT_ITERATION_PRODUCT, offset);
                if (products.isEmpty()) break;

                List<String> productStrings = products.stream().map(this::productToString).toList();
                String toWrite = String.join(DELIMITER, productStrings);

                fileWriter.append(toWrite);
                fileWriter.append(DELIMITER);

                log.info("Updated products: " + (offset + COUNT_ITERATION_PRODUCT));
            }
        } catch (IOException e) {
            log.warn(e.getMessage()); // todo
            throw new OptimizedProductPriceSchedulingResultWriteFileException(e.getMessage());
        }
    }

    private String productToString(Product product) {
        return product.toString(); // todo
    }
}
