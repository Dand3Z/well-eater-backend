package pl.well_eater.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageConfig {

    static Pageable preparePageSortedByNameAsc(int page, int size) {
        Sort sortOrder = Sort.by(Sort.Direction.ASC, "name");
        return PageRequest.of(page, size, sortOrder);
    }

    static Pageable preparePageSortedByIdAsc(int page, int size) {
        Sort sortOrder = Sort.by(Sort.Direction.ASC, "id");
        return PageRequest.of(page, size, sortOrder);
    }
}
