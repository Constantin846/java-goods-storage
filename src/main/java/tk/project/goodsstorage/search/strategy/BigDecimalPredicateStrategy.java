package tk.project.goodsstorage.search.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;

public class BigDecimalPredicateStrategy implements PredicateStrategy<BigDecimal> {
    private static final BigDecimal INCREASE_PERCENTAGE = BigDecimal.valueOf(1.2);
    private static final BigDecimal DECREASE_PERCENTAGE = BigDecimal.valueOf(0.8);

    @Override
    public Predicate searchEquals(Expression<BigDecimal> expression, BigDecimal value, CriteriaBuilder cb) {
        return cb.equal(expression, value);
    }

    @Override
    public Predicate searchMoreOrEquals(Expression<BigDecimal> expression, BigDecimal value, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate searchLessOrEquals(Expression<BigDecimal> expression, BigDecimal value, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate searchLike(Expression<BigDecimal> expression, BigDecimal value, CriteriaBuilder cb) {
        return cb.and(
                cb.greaterThanOrEqualTo(expression, value.multiply(DECREASE_PERCENTAGE)),
                cb.lessThanOrEqualTo(expression, value.multiply(INCREASE_PERCENTAGE))
        );
    }
}
