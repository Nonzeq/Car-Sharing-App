package com.kobylchak.carsharing.repository.rental;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecificationProvider implements SpecificationProvider<Rental> {
    @Override
    public String getKey() {
        return RentalKeyParameters.USER.getKey();
    }
    
    @Override
    public Specification<Rental> getSpecification(String param) {
        return (root, query, criteriaBuilder) -> root.get("user")
                                                     .get("id").in(Long.parseLong(param));
    }
}
