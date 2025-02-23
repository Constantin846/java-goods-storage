package tk.project.goodsstorage.product.dto.find.criteria;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BigDecimalCriteria extends SearchCriteria<BigDecimal> {

    @NotNull(message = "Value of search criteria must be set")
    BigDecimal value;
}
