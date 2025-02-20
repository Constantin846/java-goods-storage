package tk.project.goodsstorage.exceptions;

public class OptimizedProductPriceSchedulingSQLException extends RuntimeException {
    private final static String MESSAGE =
            "Exception during database query from optimized product price scheduler: ";
    public OptimizedProductPriceSchedulingSQLException(String message) {
        super(MESSAGE + message);
    }
}
