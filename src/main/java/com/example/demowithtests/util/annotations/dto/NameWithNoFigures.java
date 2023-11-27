package com.example.demowithtests.util.annotations.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameWithNoFiguresValidator.class)
public @interface NameWithNoFigures {
    String message() default "The name should not contain any figures or numbers";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    List<Integer> figures = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
}
