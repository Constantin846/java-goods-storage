package tk.project.goodsstorage.search.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.time.Instant;

public class InstantPredicateStrategy implements PredicateStrategy<Instant> {
    private static final Long DELTA_SECONDS_FOR_LIKE = 2 * 24 * 60 * 60L;
    private static final Long DELTA_SECONDS_FOR_EQUALS = 5 * 60 * 60L;

    @Override
    public Predicate searchEquals(Expression<Instant> expression, Instant value, CriteriaBuilder cb) {
        return cb.and(
                cb.greaterThanOrEqualTo(expression, value.minusSeconds(DELTA_SECONDS_FOR_EQUALS)),
                cb.lessThanOrEqualTo(expression, value.plusSeconds(DELTA_SECONDS_FOR_EQUALS))
        );
    }

    @Override
    public Predicate searchMoreOrEquals(Expression<Instant> expression, Instant value, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate searchLessOrEquals(Expression<Instant> expression, Instant value, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate searchLike(Expression<Instant> expression, Instant value, CriteriaBuilder cb) {
        return cb.and(
                cb.greaterThanOrEqualTo(expression, value.minusSeconds(DELTA_SECONDS_FOR_LIKE)),
                cb.lessThanOrEqualTo(expression, value.plusSeconds(DELTA_SECONDS_FOR_LIKE))
        );
    }
}
