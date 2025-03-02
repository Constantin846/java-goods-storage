package tk.project.goodsstorage.schedulers;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.exceptions.schedulers.OptimizedProductPriceSchedulingResultWriteFileException;
import tk.project.goodsstorage.exceptions.schedulers.OptimizedProductPriceSchedulingSQLException;
import tk.project.goodsstorage.timer.TaskExecutionTime;
import tk.project.goodsstorage.timer.TaskExecutionTransactionTime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

@Slf4j
@Component
@Profile("!local")
@ConditionalOnExpression("${app.scheduling.enable:false} && ${app.scheduling.optimization.enable:false}")
public class OptimizedProductPriceScheduler {
    private final String filePath;
    private static final String COMMA = ",";
    private static final String SELECT_PRODUCT = """
            SELECT *
            FROM product
            """;
    private static final String UPDATE_PRODUCT_PRICE = """
            UPDATE product
            SET price = price * (1 + ?/100)
            """; // RETURNING * - doesn't work with h2
    private static final String LOCK_PRODUCT_TABLE = "LOCK TABLE product IN ACCESS EXCLUSIVE MODE"; // doesn't work with h2
    private final BigDecimal priceIncreasePercentage;
    private final Boolean isExclusiveLocked;
    private final EntityManagerFactory entityManagerFactory;

    public OptimizedProductPriceScheduler(
            @Value("${app.scheduling.priceIncreasePercentage}")
            BigDecimal priceIncreasePercentage,
            @Value("${app.scheduling.optimization.exclusive-lock:true}")
            Boolean isExclusiveLocked,
            EntityManagerFactory entityManagerFactory
    ) {
        this.priceIncreasePercentage = priceIncreasePercentage;
        this.isExclusiveLocked = !Objects.isNull(isExclusiveLocked) && isExclusiveLocked;
        this.entityManagerFactory = entityManagerFactory;
        this.filePath = this.getClass().getClassLoader()
                .getResource("optimized-product-price-scheduling/result.csv").getPath();
    }

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

        final Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);

        try (session) {
            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    try (
                            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath));
                            connection
                    ) {
                        connection.setAutoCommit(false);

                        if (isExclusiveLocked) {
                            Statement lockStatement = connection.createStatement();
                            lockStatement.execute(LOCK_PRODUCT_TABLE);
                        }

                        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PRODUCT_PRICE);
                        preparedStatement.setBigDecimal(1, priceIncreasePercentage);
                        preparedStatement.executeUpdate();

                        PreparedStatement psSelect = connection.prepareStatement(SELECT_PRODUCT);
                        ResultSet resultSet = psSelect.executeQuery();

                        while (resultSet.next()) {
                            fileWriter.append(resultSet.getString("id")).append(COMMA)
                                    .append(resultSet.getString("name")).append(COMMA)
                                    .append(resultSet.getString("article")).append(COMMA)
                                    .append(resultSet.getString("description")).append(COMMA)
                                    .append(resultSet.getString("category")).append(COMMA)
                                    .append(resultSet.getString("price")).append(COMMA)
                                    .append(resultSet.getString("count")).append(COMMA)
                                    .append(resultSet.getString("last_count_update_time")).append(COMMA)
                                    .append(resultSet.getString("create_date"));
                            fileWriter.newLine();
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
        log.info("OPTIMIZED PRODUCT PRICE SCHEDULER finished");
    }
}
