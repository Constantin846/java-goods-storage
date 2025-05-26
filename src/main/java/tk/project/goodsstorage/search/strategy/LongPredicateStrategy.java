package tk.project.goodsstorage.search.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public class LongPredicateStrategy implements PredicateStrategy<Long> {
    private static final Long INEQUALITY = 2L;

    @Override
    public Predicate searchEquals(Expression<Long> expression, Long value, CriteriaBuilder cb) {
        return cb.equal(expression, value);
    }

    @Override
    public Predicate searchMoreOrEquals(Expression<Long> expression, Long value, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate searchLessOrEquals(Expression<Long> expression, Long value, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate searchLike(Expression<Long> expression, Long value, CriteriaBuilder cb) {
        return cb.and(
                cb.greaterThanOrEqualTo(expression, value / INEQUALITY),
                cb.lessThanOrEqualTo(expression, value * INEQUALITY)
        );
    }
}
