package com.envoy.yelpcoffee

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface YelpBusinessSearchService {
    @GET("businesses/search")
    fun searchCoffeeShop(
        @Header("Authorization") authorization: String,
        @Query("term") searchTerm :String,
        @Query("location") location:String,
        @Query("limit") limit:String,
        @Query("offset") offset:String) : Call<YelpSearchResult>
}