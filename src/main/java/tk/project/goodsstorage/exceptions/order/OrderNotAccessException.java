package tk.project.goodsstorage.exceptions.order;

public class OrderNotAccessException extends RuntimeException {
    public OrderNotAccessException(String message) {
        super(message);
    }
}
