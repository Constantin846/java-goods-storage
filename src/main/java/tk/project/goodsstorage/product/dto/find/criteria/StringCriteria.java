package tk.project.goodsstorage.product.dto.find.criteria;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StringCriteria extends SearchCriteria<String> {

    @NotBlank(message = "Value of search criteria must be set")
    String value;
}
