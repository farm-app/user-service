package ru.rtln.userservice.model.basic;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
public class PageRequestModel {

    @NotNull
    @PositiveOrZero
    protected Integer currentPage;

    @NotNull
    @Positive
    protected Integer pageSize;

    protected LocalDate startDate;
    protected LocalDate endDate;

    protected Instant startDateTime;
    protected Instant endDateTime;

    public PageRequestModel() {
        currentPage = 0;
        pageSize = 10;
    }
}