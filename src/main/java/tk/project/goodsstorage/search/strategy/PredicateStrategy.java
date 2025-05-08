package tk.project.goodsstorage.search.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public interface PredicateStrategy<T> {

    Predicate searchEquals(Expression<T> expression, T value, CriteriaBuilder cb);

    Predicate searchMoreOrEquals(Expression<T> expression, T value, CriteriaBuilder cb);

    Predicate searchLessOrEquals(Expression<T> expression, T value, CriteriaBuilder cb);

    Predicate searchLike(Expression<T> expression, T value, CriteriaBuilder cb);
}
