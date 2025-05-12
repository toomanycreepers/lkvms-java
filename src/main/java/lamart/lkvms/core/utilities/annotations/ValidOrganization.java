package lamart.lkvms.core.utilities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import lamart.lkvms.core.utilities.common.ValidOrganizationValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidOrganizationValidator.class)
public @interface ValidOrganization {
    String message() default "Organization not found in user's organizations";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
