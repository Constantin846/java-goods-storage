package tk.project.goodsstorage.product.service.criteria;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tk.project.goodsstorage.exceptions.schedulers.OperationNotDefinedByStringException;

import java.util.Arrays;

@Slf4j
@Getter
public enum Operation {

    //@JsonProperty("=")
    EQUALS("=", "ONLY"),

    //@JsonProperty(">=");
    MORE_OR_EQUALS(">=", "START_WITH"),

    //@JsonProperty("<=")
    LESS_OR_EQUALS("<=", "END_WITH"),

    //@JsonProperty("~")
    LIKE("~", "NEARLY");

    private final String symbol;
    private final String extraName;

    Operation(String symbol, String extraName) {
        this.symbol = symbol;
        this.extraName = extraName;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Operation fromString(String string) {
        return Arrays.stream(Operation.values())
                .filter(value -> value.name().equalsIgnoreCase(string)
                        || value.symbol.equals(string)
                        || value.extraName.equalsIgnoreCase(string))
                .findFirst()
                .orElseGet(() -> {
                    String message = String.format("Operation was not defined by: %s", string);
                    log.warn(message);
                    throw new OperationNotDefinedByStringException(message);
                });
    }
}
