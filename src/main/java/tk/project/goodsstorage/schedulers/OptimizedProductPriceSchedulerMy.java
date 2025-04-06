package tk.project.goodsstorage.schedulers;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.project.exceptionhandler.goodsstorage.exceptions.schedulers.OptimizedProductPriceSchedulingResultWriteFileException;
import tk.project.exceptionhandler.goodsstorage.exceptions.schedulers.OptimizedProductPriceSchedulingSQLException;
import tk.project.goodsstorage.timer.TaskExecutionTime;
import tk.project.goodsstorage.timer.TaskExecutionTransactionTime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
//@Profile("!local")
@ConditionalOnExpression("${app.scheduling.enable:false} && '${app.scheduling.optimization.type}'.equals('optimized-my')")
public class OptimizedProductPriceSchedulerMy {
    private static final Boolean IS_APPENDING_FILE = true;
    private static final String FILE_PATH = "/src/main/resources/optimized-product-price-scheduling/result.csv";
    private final String filePath;
    private static final String COMMA = ",";
    private static final Integer COUNT_ITERATION_PRODUCT = 100_000;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final String UPDATE_PRODUCT_PRICE = """
            UPDATE product p
            SET price = ?
            WHERE p.id = ?
            """;
    private static final String SELECT_PRODUCTS_LIMIT_OFFSET = """
            SELECT *
            FROM product
            LIMIT ?
            OFFSET ?
            """;
    private static final String SELECT_PRODUCTS_LIMIT_OFFSET_FOR_UPDATE = """
            SELECT *
            FROM product
            LIMIT ?
            OFFSET ?
            FOR UPDATE
            """;
    private final String selectQuery;
    private final BigDecimal priceIncreaseRate;
    private final EntityManagerFactory entityManagerFactory;

    public OptimizedProductPriceSchedulerMy(
            @Value("${app.scheduling.optimization.docker-file-path:false}")
            Boolean isDockerFilePath,
            @Value("${app.scheduling.priceIncreasePercentage}")
            BigDecimal priceIncreasePercentage,
            @Value("${app.scheduling.optimization.exclusive-lock:true}")
            Boolean isExclusiveLocked,
            EntityManagerFactory entityManagerFactory
    ) {
        this.priceIncreaseRate = priceIncreasePercentage.divide(HUNDRED, RoundingMode.HALF_EVEN).add(ONE);
        this.entityManagerFactory = entityManagerFactory;

        if (isDockerFilePath) {
            this.filePath = FILE_PATH;
        } else {
            this.filePath = this.getClass().getClassLoader()
                    .getResource("optimized-product-price-scheduling/result.csv").getPath();
        }

        if (!Objects.isNull(isExclusiveLocked) && isExclusiveLocked) {
            selectQuery = SELECT_PRODUCTS_LIMIT_OFFSET_FOR_UPDATE;
        } else {
            selectQuery = SELECT_PRODUCTS_LIMIT_OFFSET;
        }
    }

    @PostConstruct
    private void postConstruct() {
        log.info("Initialized OPTIMIZED PRODUCT PRICE SCHEDULER MY");
    }

    @TaskExecutionTime
    @TaskExecutionTransactionTime
    @Transactional
    @Scheduled(fixedDelayString = "${app.scheduling.period}")
    public void increaseProductPrice() {
        log.info("OPTIMIZED PRODUCT PRICE SCHEDULER MY is running");

        final Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);

        try (session) {
            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    try (
                            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath, IS_APPENDING_FILE));
                            connection
                    ) {
                        connection.setAutoCommit(false);
                        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PRODUCT_PRICE);
                        PreparedStatement psSelect = connection.prepareStatement(selectQuery);
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
                                        .append(resultSet.getString("create_date"));
                                fileWriter.newLine();
                            }
                        }
                        connection.commit();

                    } catch (SQLException e) {
                        connection.rollback();
                        throw new OptimizedProductPriceSchedulingSQLException(e.getMessage());
                    } catch (IOException e) {
                        connection.rollback();
                        throw new OptimizedProductPriceSchedulingResultWriteFileException(e.getMessage());
                    }
                }
            });
        }
        log.info("OPTIMIZED PRODUCT PRICE SCHEDULER MY finished");
    }
}
