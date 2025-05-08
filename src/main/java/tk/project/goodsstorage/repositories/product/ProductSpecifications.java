package tk.project.goodsstorage.repositories.product;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.models.product.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class ProductSpecifications {

    public static Specification<Product> hasProductStringFieldEquals(final String field, final List<String> values) {
        return ((root, query, criteriaBuilder) -> root.get(field).in(values));
    }

    public static Specification<Product> hasProductStringFieldStartWith(final String field, final String value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), value + "%"));
    }

    public static Specification<Product> hasProductStringFieldEndWith(final String field, final String value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), "%" + value));
    }

    public static Specification<Product> hasProductStringFieldLike(final String field, final String value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), "%" + value + "%"));
    }

    public static Specification<Product> hasProductNumberFieldEquals(final String field, final List<Number> values) {
        return ((root, query, criteriaBuilder) -> root.get(field).in(values));
    }

    public static Specification<Product> hasProductPriceMoreOrEquals(final BigDecimal price) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price));
    }

    public static Specification<Product> hasProductPriceLessOrEquals(final BigDecimal price) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), price));
    }

    public static Specification<Product> hasProductCountMoreOrEquals(final Long count) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("count"), count));
    }

    public static Specification<Product> hasProductCountLessOrEquals(final Long count) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("count"), count));
    }

    public static Specification<Product> hasProductLastCountUpdateTimeAfter(final Instant lastCountUpdateTime) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("lastCountUpdateTime"), lastCountUpdateTime));
    }

    public static Specification<Product> hasProductLastCountUpdateTimeBefore(final Instant lastCountUpdateTime) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("lastCountUpdateTime"), lastCountUpdateTime));
    }

    public static Specification<Product> hasProductCreateDateEquals(final LocalDate createDate) {
        return ((root, query, criteriaBuilder) -> root.get("createDate").in(createDate));
    }

    public static Specification<Product> hasProductCreateDateAfter(final LocalDate createDate) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), createDate));
    }

    public static Specification<Product> hasProductCreateDateBefore(final LocalDate createDate) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createDate"), createDate));
    }
}
