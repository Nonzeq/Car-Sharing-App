package com.kobylchak.carsharing.repository.rental;

import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.repository.SpecificationProvider;
import com.kobylchak.carsharing.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalSpecificationProviderManager implements SpecificationProviderManager<Rental> {
    private final List<SpecificationProvider<Rental>> specificationProviders;
    
    @Override
    public SpecificationProvider<Rental> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                       .filter(provider -> provider.getKey().equals(key))
                       .findFirst()
                       .orElseThrow(
                               () -> new IllegalArgumentException("No specification "
                                                                  + "provider found for key: "
                                                                  + key));
    }
}
