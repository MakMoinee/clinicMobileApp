package com.sample.clinic.Models;


import com.sample.clinic.Common.Constants;

import lombok.Data;

@Data
public class Photos {
    int height;
    String photo_reference;
    int width;


    public String getPhotoUrl() {
        String fUrl = Constants.photoURL + String.format("?maxwidth=720&photo_reference=%s&key=%s", this.photo_reference, Constants.gmapKey);
        return fUrl;
    }
}
