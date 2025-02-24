package tk.project.goodsstorage.product.service.criteria;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.exceptions.ProductSpecificationException;
import tk.project.goodsstorage.product.dto.find.criteria.SearchCriteria;
import tk.project.goodsstorage.product.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SearchCriteriaManager {
    private static final String NAME = "name";
    private static final String ARTICLE = "article";
    private static final String DESCRIPTION = "description";
    private static final String CATEGORY = "category";
    private static final String PRICE = "price";
    private static final String COUNT = "count";
    private static final String LAST_COUNT_UPDATE_TIME = "lastCountUpdateTime";
    private static final String CREATE_DATE = "createDate";
    private final Map<String, ProductFieldCriteriaManager> criteriaManagers;

    public SearchCriteriaManager() {
        criteriaManagers = new HashMap<>();

        StringCriteriaManager stringCriteriaManager = new StringCriteriaManager();
        criteriaManagers.put(NAME, stringCriteriaManager);
        criteriaManagers.put(ARTICLE, stringCriteriaManager);
        criteriaManagers.put(DESCRIPTION, stringCriteriaManager);
        criteriaManagers.put(CATEGORY, stringCriteriaManager);

        PriceCriteriaManager priceCriteriaManager = new PriceCriteriaManager();
        criteriaManagers.put(PRICE, priceCriteriaManager);

        CountCriteriaManager countCriteriaManager = new CountCriteriaManager();
        criteriaManagers.put(COUNT, countCriteriaManager);

        LastCountUpdateTimeCriteriaManager lastCountUpdateTimeCriteriaManager = new LastCountUpdateTimeCriteriaManager();
        criteriaManagers.put(LAST_COUNT_UPDATE_TIME, lastCountUpdateTimeCriteriaManager);

        CreateDateCriteriaManager createDateCriteriaManager = new CreateDateCriteriaManager();
        criteriaManagers.put(CREATE_DATE, createDateCriteriaManager);
    }

    public Specification<Product> getSpecification(List<SearchCriteria<?>> criteria) {
        List<Specification<Product>> specifications = new ArrayList<>();
        Specification<Product> specification;

        for (SearchCriteria<?> item : criteria) {
            if (criteriaManagers.containsKey(item.getField())) {
                specification = criteriaManagers.get(item.getField()).getSpecification(item);
                specifications.add(specification);
            }
        }
        return specifications.stream().reduce(Specification::and)
                .orElseGet(() -> {
                    String message = "Not defined product specification for search";
                    log.warn(message);
                    throw new ProductSpecificationException(message);
                });
    }
}
