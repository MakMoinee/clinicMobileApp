package com.sample.clinic.Models;

import java.util.List;

import lombok.Data;

@Data
public class FullNearPlacesResponse {
    List<NearPlacesResponse> results;
}
