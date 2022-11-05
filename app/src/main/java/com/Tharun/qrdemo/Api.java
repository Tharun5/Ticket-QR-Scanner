package com.Tharun.qrdemo;

import retrofit2.Call;
import retrofit2.http.PATCH;
import retrofit2.http.Query;

public interface Api {
    // baseurl/link/?tick=id
    @PATCH("link")
    Call<ReturnObject> getResponse(
            @Query("tick") int id
    );

}