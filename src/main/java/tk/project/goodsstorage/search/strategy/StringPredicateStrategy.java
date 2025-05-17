package tk.project.goodsstorage.search.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public class StringPredicateStrategy implements PredicateStrategy<String> {

    @Override
    public Predicate searchEquals(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.equal(expression, value);
    }

    @Override
    public Predicate searchMoreOrEquals(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.like(expression, value + "%");
    }

    @Override
    public Predicate searchLessOrEquals(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.like(expression, "%" + value);
    }

    @Override
    public Predicate searchLike(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.like(expression, "%" + value + "%");
        //return cb.like(cb.lower(expression), "%" + value.toLowerCase() + "%");
    }
}
