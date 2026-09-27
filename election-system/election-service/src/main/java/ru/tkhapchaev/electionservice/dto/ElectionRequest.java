package ru.tkhapchaev.electionservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ElectionRequest(
        @NotBlank String name,
        String description,
        @NotNull Integer statusId,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double quorum
) {
}
