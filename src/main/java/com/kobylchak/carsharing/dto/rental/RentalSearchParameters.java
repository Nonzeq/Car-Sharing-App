package com.kobylchak.carsharing.dto.rental;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kobylchak.carsharing.dto.SearchParameters;
import com.kobylchak.carsharing.repository.rental.RentalKeyParameters;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class RentalSearchParameters implements SearchParameters {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("is_active")
    private String isActive;
    
    @Override
    public Map<String, String> getParameters() {
        HashMap<String, String> params = new HashMap<>();
        params.put(RentalKeyParameters.USER.getKey(), userId);
        params.put(RentalKeyParameters.IS_ACTIVE.getKey(), isActive);
        return params;
    }
}
