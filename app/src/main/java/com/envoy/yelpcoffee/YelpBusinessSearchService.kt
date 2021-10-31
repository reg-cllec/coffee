package com.envoy.yelpcoffee

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * yelp api call for coffee shop search
 * https://web.postman.co/workspace/My-Workspace~22ff0670-a7fb-4b3a-8e6f-c6e5be6b4f5e/request/6768106-a74cb715-c541-4fcc-a9ab-14e34ec1f933
 */
interface YelpBusinessSearchService {
    @GET("businesses/search")
    fun searchCoffeeShop(
        @Header("Authorization") authorization: String,
        @Query("term") searchTerm :String,
        @Query("location") location:String,
        @Query("limit") limit:String,
        @Query("offset") offset:String) : Call<YelpSearchResult>
}