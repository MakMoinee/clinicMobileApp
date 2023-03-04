package com.sample.clinic.Services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sample.clinic.Common.Constants;
import com.sample.clinic.Interfaces.LocalRequestListener;
import com.sample.clinic.Models.FullNearPlacesResponse;
import com.sample.clinic.Models.NearPlacesRequest;

public class LocalRequest {

    Context mContext;

    public LocalRequest(Context mContext) {
        this.mContext = mContext;
    }

    public void getNearbyHospitals(NearPlacesRequest req, LocalRequestListener listener) {

        String fUrl = String.format(Constants.getHospitalNearMeURL + "?location=%s&keyword=hospitals near me &radius=1500&type=hospital&key=%s", req.getLocation(), Constants.gmapKey);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, fUrl, response -> {
            if (response.equals("")) {
                listener.onError();
            } else {
                FullNearPlacesResponse nearPlacesResponse = new Gson().fromJson(response, new TypeToken<FullNearPlacesResponse>() {
                }.getType());
                if (nearPlacesResponse != null) {
                    listener.onSuccess(nearPlacesResponse);
                }
            }
        }, error -> {
            Log.e("ERROR_GET_NEARBY", error.getMessage());
            listener.onError();
        });

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(stringRequest);
    }
}
