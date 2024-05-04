package com.kobylchak.carsharing.service.rental;

import com.kobylchak.carsharing.model.Rental;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class RentalMessageGeneratorImpl implements RentalMessageGenerator {
    
    @Override
    public String getForCreating(Rental rental) {
        return "<b>Created new Rental</b>"
               + System.lineSeparator()
               + "<b>ID</b>: " + rental.getId()
               + System.lineSeparator()
               + "<b>Date</b>: " + rental.getRentalDate()
               + System.lineSeparator()
               + "<b>Car brand</b>: " + rental.getCar().getBrand()
               + System.lineSeparator()
               + "<b>Car model</b>: " + rental.getCar().getModel()
               + System.lineSeparator()
               + "<b>Return date</b>: " + rental.getReturnDate()
               + System.lineSeparator()
               + "<b>User email</b>: " + rental.getUser().getEmail();
    }
    
    @Override
    public String getForOverdue(Rental rental) {
        return "<b>Overdue Rental</b>"
               + System.lineSeparator()
               + "<b>ID</b>: " + rental.getId()
               + System.lineSeparator()
               + "<b>Date</b>: " + rental.getRentalDate()
               + System.lineSeparator()
               + "<b>Car brand</b>: " + rental.getCar().getBrand()
               + System.lineSeparator()
               + "<b>Car model</b>: " + rental.getCar().getModel()
               + System.lineSeparator()
               + "<b>Return date</b>: " + rental.getReturnDate()
               + System.lineSeparator()
               + "<b>Overdue days</b>: "
               + (LocalDate.now().getDayOfYear() - rental.getRentalDate().getDayOfYear())
               + System.lineSeparator()
               + "<b>User email</b>: " + rental.getUser().getEmail();
    }
    
    @Override
    public String getForNotOverdue(Rental rental) {
        return  "No rentals overdue today!" ;
    }
}
