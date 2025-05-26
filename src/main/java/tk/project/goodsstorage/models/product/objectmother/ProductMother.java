package tk.project.goodsstorage.models.product.objectmother;

public class ProductMother {
    public static ProductBuilder createDefaultProduct() {
        return ProductBuilder.aProduct();
    }
}
