package com.kobylchak.carsharing.dto.rental;

import com.kobylchak.carsharing.dto.SearchParameters;
import com.kobylchak.carsharing.repository.rental.RentalKeyParameters;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class RentalSearchParameters implements SearchParameters {
    private String userId;
    private String isActive;
    
    @Override
    public Map<String, String> getParameters() {
        HashMap<String, String> params = new HashMap<>();
        params.put(RentalKeyParameters.USER.getKey(), userId);
        params.put(RentalKeyParameters.IS_ACTIVE.getKey(), isActive);
        return params;
    }
}
