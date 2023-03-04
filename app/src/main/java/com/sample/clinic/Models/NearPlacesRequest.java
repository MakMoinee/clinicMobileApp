package com.sample.clinic.Models;

import lombok.Data;

@Data
public class NearPlacesRequest {
    String keyword;
    String location;
    int radius;
    String type;
    String key;
}
