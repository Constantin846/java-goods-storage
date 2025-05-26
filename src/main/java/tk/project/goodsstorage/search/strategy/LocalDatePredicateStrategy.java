package tk.project.goodsstorage.search.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;

public class LocalDatePredicateStrategy implements PredicateStrategy<LocalDate> {
    private static final Long DELTA_DAYS_FOR_LIKE = 3L;

    @Override
    public Predicate searchEquals(Expression<LocalDate> expression, LocalDate value, CriteriaBuilder cb) {
        return cb.equal(expression, value);
    }

    @Override
    public Predicate searchMoreOrEquals(Expression<LocalDate> expression, LocalDate value, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate searchLessOrEquals(Expression<LocalDate> expression, LocalDate value, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate searchLike(Expression<LocalDate> expression, LocalDate value, CriteriaBuilder cb) {
        return cb.and(
                cb.greaterThanOrEqualTo(expression, value.minusDays(DELTA_DAYS_FOR_LIKE)),
                cb.lessThanOrEqualTo(expression, value.plusDays(DELTA_DAYS_FOR_LIKE))
        );
    }
}
