package tk.project.goodsstorage.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tk.project.goodsstorage.annotations.NullOrNotBlank;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {
    @Override
    public void initialize(NullOrNotBlank constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String field, final ConstraintValidatorContext constraintValidatorContext) {
        if (field == null) return true;
        return !field.isBlank();
    }
}
