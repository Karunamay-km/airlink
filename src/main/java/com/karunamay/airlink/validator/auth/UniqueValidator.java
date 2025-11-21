package com.karunamay.airlink.validator.auth;

import com.karunamay.airlink.repository.user.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueValidator implements ConstraintValidator<Unique, String> {

    private final UserRepository userRepository;

    private String fieldName;

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        boolean exists;

        context.disableDefaultConstraintViolation();

        switch(fieldName) {
            case "username":
                exists = userRepository.existsByUsername(value);
                context.buildConstraintViolationWithTemplate("username " + value + " already exists")
                        .addConstraintViolation();
                break;
            case "email":
                exists = userRepository.existsByEmail(value);
                context.buildConstraintViolationWithTemplate("Email " + value + " already exists")
                        .addConstraintViolation();
                break;
            default:
                exists = false;
        }

        return !exists;
    }
}
