package com.sample.clinic.Models;

import java.util.List;

import lombok.Data;

@Data
public class NearPlacesResponse {
    String business_status;
    List<Geometry> geometry;
    String name;
    List<Photos> photos;

}
