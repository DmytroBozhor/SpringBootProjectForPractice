package com.example.demowithtests.util.annotations.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.function.Predicate;

public class NameWithNoFiguresValidator implements ConstraintValidator<NameWithNoFigures, String> {
    private List<Integer> figures;

    @Override
    public void initialize(NameWithNoFigures constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        figures = constraintAnnotation.figures;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return figures.stream().noneMatch(integer -> value.contains(integer.toString()));
    }
}
