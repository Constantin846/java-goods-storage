package tk.project.goodsstorage.exceptions.order;

public class OrderStatusAlreadyRejectedException extends RuntimeException {
    public OrderStatusAlreadyRejectedException(String message) {
        super(message);
    }
}
