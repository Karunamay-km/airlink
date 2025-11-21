package com.karunamay.airlink.validator.auth;

import com.karunamay.airlink.dto.user.RegistrationRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PasswordsValidator implements ConstraintValidator<PasswordValidation, RegistrationRequestDTO> {

    private String fieldName;
    private String message;

    @Override
    public void initialize(PasswordValidation constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(RegistrationRequestDTO value, ConstraintValidatorContext context) {
        RegistrationRequestDTO request = (RegistrationRequestDTO) value;
        boolean valid = request.getPassword().equals(request.getConfirmPassword());
        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(fieldName)
                    .addConstraintViolation();
        }
        return valid;
    }
}
