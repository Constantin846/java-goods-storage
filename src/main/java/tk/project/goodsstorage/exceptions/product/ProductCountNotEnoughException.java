package tk.project.goodsstorage.exceptions.product;

public class ProductCountNotEnoughException extends RuntimeException {
    public ProductCountNotEnoughException(String message) {
        super(message);
    }
}
