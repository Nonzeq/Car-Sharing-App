package com.kobylchak.carsharing.controller;

import com.kobylchak.carsharing.dto.rental.CreateRentalRequestDto;
import com.kobylchak.carsharing.dto.rental.RentalDto;
import com.kobylchak.carsharing.dto.rental.RentalSearchParameters;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.service.rental.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/rentals")
@RequiredArgsConstructor
@Tag(
        name = "Rentals management",
        description = "Endpoints for rentals management"
)
public class RentalController {
    private final RentalService rentalService;
    
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(summary = "Get list of all available rentals with query parameters "
                         + "(userId : number, isActive: boolean) ")
    public List<RentalDto> searchRentals(@Valid RentalSearchParameters searchParameters,
                                         Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.getRentalsByParameters(searchParameters, user);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new rental")
    public RentalDto createRental(@RequestBody @Valid CreateRentalRequestDto requestDto,
                                  Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.createRental(requestDto, user);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public RentalDto getRental(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.getRentalById(id, user);
    }
    
    @GetMapping("/{id}/return")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return a payment by id")
    public RentalDto returnRental(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.returnRental(id, user);
    }
    
}
