package tk.project.goodsstorage.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ArticleExistsException extends RuntimeException {
    private final UUID existedProductId;

    public ArticleExistsException(String message, UUID existedProductId) {
        super(message);
        this.existedProductId = existedProductId;
    }
}
