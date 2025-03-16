package tk.project.goodsstorage.exceptions.kafka;

public class EventHandlerNotFoundException extends RuntimeException {
    public EventHandlerNotFoundException(String message) {
        super(message);
    }
}
