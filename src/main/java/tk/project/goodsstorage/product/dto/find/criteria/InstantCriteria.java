package tk.project.goodsstorage.product.dto.find.criteria;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstantCriteria extends SearchCriteria<Instant> {

    @NotNull(message = "Value of search criteria must be set") // format "2025-02-22T14:32:00Z"
    Instant value;
}
