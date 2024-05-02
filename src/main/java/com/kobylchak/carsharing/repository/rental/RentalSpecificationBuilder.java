package com.kobylchak.carsharing.repository.rental;

import com.kobylchak.carsharing.dto.SearchParameters;
import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.repository.SpecificationBuilder;
import com.kobylchak.carsharing.repository.SpecificationProviderManager;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    private final SpecificationProviderManager<Rental> rentalSpecificationProviderManager;
    
    @Override
    public Specification<Rental> build(SearchParameters searchParameters) {
        Specification<Rental> spec = Specification.where(getSpecWithUserAndCar());
        Map<String, String> parameters = searchParameters.getParameters();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            spec = setSpec(entry, spec);
        }
        return spec;
    }
    
    private Specification<Rental> setSpec(Map.Entry<String, String> entry,
                                          Specification<Rental> spec) {
        String key = entry.getKey();
        String value = entry.getValue();
        if (value != null && !value.isEmpty()) {
            spec = spec.and(
                    rentalSpecificationProviderManager
                            .getSpecificationProvider(key)
                            .getSpecification(value)
                           );
        }
        return spec;
    }
    
    private Specification<Rental> getSpecWithUserAndCar() {
        return (root, query, criteriaBuilder) -> {
            root.fetch("user");
            root.fetch("car");
            return null;
        };
    }
}
