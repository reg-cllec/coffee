package com.envoy.yelpcoffee

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * yelp api call for coffee shop details
 * https://api.yelp.com/v3/businesses/golden-goat-coffee-san-francisco
 */
interface YelpBusinessService {
        @GET("businesses/{id}")
        fun getCoffeeShopById(
            @Header("Authorization") authorization: String,
            @Path("id") id:String) : Call<YelpCoffeeShopDetailResult>
}