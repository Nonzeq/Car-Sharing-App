package com.kobylchak.carsharing.repository;

import com.kobylchak.carsharing.dto.SearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(SearchParameters searchParameters);
}
