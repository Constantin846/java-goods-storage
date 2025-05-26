package tk.project.goodsstorage.repositories.product;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.search.criteria.SearchCriteria;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ProductSpecification implements Specification<Product> {
    private final List<SearchCriteria> criteriaList;

    @Override
    public Predicate toPredicate(
            @NotNull Root<Product> root,
            @NotNull CriteriaQuery<?> query,
            @NotNull CriteriaBuilder criteriaBuilder
    ) {
        final List<Predicate> predicates = criteriaList.stream().map(it -> {

            switch (it.getOperation()) {
                case EQUALS -> {
                    return it.getStrategy().searchEquals(
                            root.get(it.getField()), it.getValue(), criteriaBuilder);
                }
                case MORE_OR_EQUALS -> {
                    return it.getStrategy().searchMoreOrEquals(
                            root.get(it.getField()), it.getValue(), criteriaBuilder);
                }
                case LESS_OR_EQUALS -> {
                    return it.getStrategy().searchLessOrEquals(
                            root.get(it.getField()), it.getValue(), criteriaBuilder);
                }
                case LIKE -> {
                    return it.getStrategy().searchLike(
                            root.get(it.getField()), it.getValue(), criteriaBuilder);
                }
                default -> throw new IllegalStateException("Unexpected value " + it.getOperation());
            }
        }).toList();

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
