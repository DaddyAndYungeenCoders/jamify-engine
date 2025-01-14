package com.jamify_engine.engine.models.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddressType {

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^[0-9]{5}$", message = "Invalid zip code format (must be 5 digits)")
    private String zipCode;
}
