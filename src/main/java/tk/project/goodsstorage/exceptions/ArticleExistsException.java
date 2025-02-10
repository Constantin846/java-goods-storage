package tk.project.goodsstorage.exceptions;

public class ArticleExistsException extends RuntimeException {
    public ArticleExistsException(String message) {
        super(message);
    }
}
