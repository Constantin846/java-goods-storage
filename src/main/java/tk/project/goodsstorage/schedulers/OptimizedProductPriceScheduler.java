package tk.project.goodsstorage.schedulers;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.exceptions.OptimizedProductPriceSchedulingResultWriteFileException;
import tk.project.goodsstorage.exceptions.OptimizedProductPriceSchedulingSQLException;
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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
//@Profile("!local")
@ConditionalOnExpression("${app.scheduling.enable:false} && ${app.scheduling.optimization.enable:false}")
public class OptimizedProductPriceScheduler {
    private final String filePath = this.getClass().getClassLoader()
            .getResource("optimized-product-price-scheduling/result.csv").getPath();
    private static final String DELIMITER = "\n";
    private static final String COMMA = ",";
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
                PreparedStatement psSelect = conn.prepareStatement(SELECT_PRODUCTS_LIMIT_OFFSET);
                psSelect.setInt(1, COUNT_ITERATION_PRODUCT);

                for (int pageNumber = 0; ; pageNumber++) {
                    int offset = pageNumber * COUNT_ITERATION_PRODUCT;
                    psSelect.setInt(2, offset);
                    ResultSet resultSet = psSelect.executeQuery();
                    if (!resultSet.isBeforeFirst()) break;

                    while (resultSet.next()) {
                        UUID productId = UUID.fromString(resultSet.getString("id"));
                        BigDecimal productPrice = resultSet.getBigDecimal("price");
                        productPrice = productPrice.multiply(priceIncreaseRate);

                        preparedStatement.setBigDecimal(1, productPrice);
                        preparedStatement.setObject(2, productId);
                        preparedStatement.executeUpdate();

                        fileWriter.append(productId.toString()).append(COMMA)
                                .append(resultSet.getString("name")).append(COMMA)
                                .append(resultSet.getString("article")).append(COMMA)
                                .append(resultSet.getString("description")).append(COMMA)
                                .append(resultSet.getString("category")).append(COMMA)
                                .append(productPrice.toString()).append(COMMA)
                                .append(resultSet.getString("count")).append(COMMA)
                                .append(resultSet.getString("last_count_update_time")).append(COMMA)
                                .append(resultSet.getString("create_date")).append(DELIMITER);
                    }
                    log.info("Updated products: " + (offset + COUNT_ITERATION_PRODUCT));
                }
                conn.commit();
            } catch (SQLException e) {
                throw new OptimizedProductPriceSchedulingSQLException(e.getMessage());
            }
        } catch (IOException e) {
            throw new OptimizedProductPriceSchedulingResultWriteFileException(e.getMessage());
        }

        log.info("OPTIMIZED PRODUCT PRICE SCHEDULER finished");
    }

    private BigDecimal calculatePriceIncreaseRate() {
        return this.priceIncreasePercentage.divide(HUNDRED).add(ONE);
    }
}
