package tk.project.goodsstorage.product.repository;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.model.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class ProductSpecifications { // todo update

    public static Specification<Product> hasProductStringFieldEquals(String field, List<String> values) {
        return ((root, query, criteriaBuilder) -> root.get(field).in(values));
    }

    public static Specification<Product> hasProductStringFieldStartWith(String field, String value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), value + "%"));
    }

    public static Specification<Product> hasProductStringFieldEndWith(String field, String value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), "%" + value));
    }

    public static Specification<Product> hasProductStringFieldLike(String field, String value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), "%" + value + "%"));
    }

    public static Specification<Product> hasProductNumberFieldEquals(String field, List<Number> values) {
        return ((root, query, criteriaBuilder) -> root.get(field).in(values));
    }

    public static Specification<Product> hasProductPriceMoreOrEquals(BigDecimal price) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price));
    }

    public static Specification<Product> hasProductPriceLessOrEquals(BigDecimal price) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), price));
    }

    public static Specification<Product> hasProductCountMoreOrEquals(Long count) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("count"), count));
    }

    public static Specification<Product> hasProductCountLessOrEquals(Long count) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("count"), count));
    }

    public static Specification<Product> hasProductLastCountUpdateTimeAfter(Instant lastCountUpdateTime) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("lastCountUpdateTime"), lastCountUpdateTime));
    }

    public static Specification<Product> hasProductLastCountUpdateTimeBefore(Instant lastCountUpdateTime) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("lastCountUpdateTime"), lastCountUpdateTime));
    }

    public static Specification<Product> hasProductCreateDateEquals(LocalDate createDate) {
        return ((root, query, criteriaBuilder) -> root.get("createDate").in(createDate));
    }

    public static Specification<Product> hasProductCreateDateAfter(LocalDate createDate) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), createDate));
    }

    public static Specification<Product> hasProductCreateDateBefore(LocalDate createDate) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createDate"), createDate));
    }
}
