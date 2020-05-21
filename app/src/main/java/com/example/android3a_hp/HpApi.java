package com.example.android3a_hp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


public interface HpApi {
    @GET("api_HP_personnage.json")
    Call<List<HP_personnage>> getHP_personnage();
}
