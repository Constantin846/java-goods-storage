package tk.project.goodsstorage.product.service.criteria;

import org.springframework.data.jpa.domain.Specification;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;

public interface ItemCriteriaManager<T> {

    Specification<Product> getSpecification(SearchCriteria<T> criteria);
}
