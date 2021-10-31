package com.kathleenwang.simpleyelp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


public interface YelpBusinessSearchService {
    @GET("businesses/search")
    fun searchRestaurant(
        @Header("Authorization") authorization: String,
        @Query("term") searchTerm :String,
        @Query("location") location:String,
        @Query("limit") limit:String) :
            // what we get back:
            Call<YelpSearchResult>
}