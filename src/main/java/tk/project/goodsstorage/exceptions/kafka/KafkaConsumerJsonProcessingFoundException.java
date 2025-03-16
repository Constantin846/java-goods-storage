package tk.project.goodsstorage.exceptions.kafka;

public class KafkaConsumerJsonProcessingFoundException extends RuntimeException {
    public KafkaConsumerJsonProcessingFoundException(String message) {
        super(message);
    }
}
