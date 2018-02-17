package com.auth0.samples;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Yuriy on 28.01.2018.
 */

public interface DevDayApi {
    @GET("DevDayAuthBackend/api/profile")
    Call<ApiResponse> getUserProfile();
}
