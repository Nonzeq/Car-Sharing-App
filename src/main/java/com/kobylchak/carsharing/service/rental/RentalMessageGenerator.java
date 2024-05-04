package com.kobylchak.carsharing.service.rental;

import com.kobylchak.carsharing.model.Rental;

public interface RentalMessageGenerator {
    String getForCreating(Rental rental);
    
    String getForOverdue(Rental rental);
    
    String getForNotOverdue(Rental rental);
}
