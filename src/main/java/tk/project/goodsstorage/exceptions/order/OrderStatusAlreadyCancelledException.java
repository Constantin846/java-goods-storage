package tk.project.goodsstorage.exceptions.order;

public class OrderStatusAlreadyCancelledException extends RuntimeException {
    public OrderStatusAlreadyCancelledException(String message) {
        super(message);
    }
}
