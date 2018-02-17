package com.auth0.samples;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Yuriy on 28.01.2018.
 */

public class ApiResponse {

    @SerializedName("answer")
    @Expose
    public String Answer;
}
