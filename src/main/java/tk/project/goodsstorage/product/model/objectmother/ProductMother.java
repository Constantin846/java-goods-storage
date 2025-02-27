package tk.project.goodsstorage.product.model.objectmother;

public class ProductMother {
    public static ProductBuilder createDefaultProduct() {
        return ProductBuilder.aProduct();
    }
}
