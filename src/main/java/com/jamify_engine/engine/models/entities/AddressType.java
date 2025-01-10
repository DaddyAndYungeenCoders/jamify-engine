package com.jamify_engine.engine.models.entities;

import lombok.Data;

@Data
public class AddressType {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}
