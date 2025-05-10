package ru.rtln.userservice.model.basic;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class PageModel<T> {

    private List<? extends T> content;

    private Integer currentPage;

    private Integer pageSize;

    private Integer totalPages;

    private Long totalElements;
}
