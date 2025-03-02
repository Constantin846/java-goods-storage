package tk.project.goodsstorage.product.model.objectmother;

import tk.project.goodsstorage.product.model.CategoryType;
import tk.project.goodsstorage.product.model.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class ProductBuilder {
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final CategoryType CATEGORY = CategoryType.UNDEFINED;
    private static final BigDecimal PRICE = BigDecimal.valueOf(100.0);
    private static final Long COUNT = 10L;
    private UUID id;
    private String name = NAME;
    private String article = UUID.randomUUID().toString();;
    private String description = DESCRIPTION;
    private CategoryType category = CATEGORY;
    private BigDecimal price = PRICE;
    private Long count = COUNT;
    private Instant lastCountUpdateTime = Instant.now();
    private LocalDate createDate = LocalDate.now();

    private ProductBuilder() {
    }

    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    public ProductBuilder withId(UUID id) {
        this.id = id;
        return this;
    }
    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }
    public ProductBuilder withArticle(String article) {
        this.article = article;
        return this;
    }
    public ProductBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    public ProductBuilder withCategory(CategoryType category) {
        this.category = category;
        return this;
    }
    public ProductBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
    public ProductBuilder withCount(Long count) {
        this.count = count;
        return this;
    }
    public ProductBuilder withLastCountUpdateTime(Instant lastCountUpdateTime) {
        this.lastCountUpdateTime = lastCountUpdateTime;
        return this;
    }
    public ProductBuilder withCreateDate(LocalDate createDate) {
        this.createDate = createDate;
        return this;
    }

    public Product build() {
        return Product.builder()
                .id(this.id)
                .name(this.name)
                .article(this.article)
                .description(this.description)
                .category(this.category)
                .price(this.price)
                .count(this.count)
                .lastCountUpdateTime(this.lastCountUpdateTime)
                .createDate(this.createDate)
                .build();
    }
}
