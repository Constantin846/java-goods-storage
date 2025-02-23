package tk.project.goodsstorage.product.dto.find.criteria;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalDateCriteria extends SearchCriteria<LocalDate> {

    @NotNull(message = "Value of search criteria must be set")
    LocalDate value;
}
