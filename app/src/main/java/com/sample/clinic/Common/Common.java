package com.sample.clinic.Common;

import com.sample.clinic.Models.Buildings;
import com.sample.clinic.Models.Users;

import java.util.HashMap;
import java.util.Map;

public class Common {

    public static Map<String, Object> toLoginMaps(Users users) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", users.getEmail());
        params.put("password", users.getPassword());
        params.put("firstName", users.getFirstName());
        params.put("middleName", users.getMiddleName());
        params.put("lastName", users.getLastName());
        params.put("secret", users.getSecret());
        params.put("userType", users.getUserType());
        return params;
    }

    public static Map<String, Object> toBuildingMaps(Buildings buildings) {
        Map<String, Object> params = new HashMap<>();
        params.put("buildingName", buildings.getBuildingName());
        params.put("description", buildings.getDescription());
        params.put("picturePath", buildings.getPicturePath());
        params.put("videoPath", buildings.getVideoPath());
        return params;
    }
}
