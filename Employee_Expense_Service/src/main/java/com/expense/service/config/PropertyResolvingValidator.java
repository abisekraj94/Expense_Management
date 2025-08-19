package com.expense.service.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.regex.PatternSyntaxException;

@Component
public class PropertyResolvingValidator implements ConstraintValidator<Pattern, String> {

    @Autowired
    private Environment environment;

    private java.util.regex.Pattern pattern;

    @Override
    public void initialize(Pattern constraintAnnotation) {
        String regexp = constraintAnnotation.regexp();
        if (regexp.startsWith("${") && regexp.endsWith("}")) {
            String propertyName = regexp.substring(2, regexp.length() - 1);
            regexp = environment.getProperty(propertyName, regexp);
        }
        try {
            pattern = java.util.regex.Pattern.compile(regexp);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regular expression: " + regexp, e);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return pattern.matcher(value).matches();
    }
}