package tk.project.goodsstorage.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotBlank {
    String message() default "Field must be null or not blank";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
