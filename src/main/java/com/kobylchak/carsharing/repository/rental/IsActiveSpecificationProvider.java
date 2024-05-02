package com.kobylchak.carsharing.repository.rental;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsActiveSpecificationProvider implements SpecificationProvider<Rental> {
    @Override
    public String getKey() {
        return RentalKeyParameters.IS_ACTIVE.getKey();
    }
    
    @Override
    public Specification<Rental> getSpecification(String param) {
        return (root, query, criteriaBuilder) -> root.get(getKey()).in(Boolean.parseBoolean(param));
    }
}
