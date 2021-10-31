package com.envoy.yelpcoffee

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface YelpBusinessService {
        @GET("businesses/{id}")
        fun getCoffeeShopById(
            @Header("Authorization") authorization: String,
            @Path("id") id:String) : Call<YelpCoffeeShopDetailResult>
}