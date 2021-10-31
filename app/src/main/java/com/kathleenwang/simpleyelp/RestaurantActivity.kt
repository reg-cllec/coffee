package com.kathleenwang.simpleyelp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_restaurant.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.yelp.com/v3/"
const val AUTHORIZATION =
    "Bearer CzNDCfhkDjG0KNQelF5zalA7nsgBLVwRwuBsTSa0tdjmxwq-NV4PioO-bs3mdaNWLn-hyNNv3-p4iCTv9XApL1wq1JjJ3dIIZDTKBKlYYBCgz6CAGlmCXMpnAo99YXYx"
const val INTENT_EXTRA_RESTAURANT = "restaurant"
private const val TAG = "MainActivity"
private const val TERM = "coffee"
private const val LOCATION = "410 Townsend Street, San Francisco, CA"
private const val LIMIT = "10"

class RestaurantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        getRestaurant()
    }

    private fun getRestaurant() {
        val restaurants = mutableListOf<YelpRestaurants>()
        val adapter = RestaurantsAdapter(this, restaurants)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val yelpBusinessSearchService = retrofit.create(YelpBusinessSearchService::class.java)

        requestSearchResult(yelpBusinessSearchService, restaurants, adapter)

        setRecyclerView(adapter)
        setRecyclerViewOnScrollListener(yelpBusinessSearchService, restaurants, adapter)
    }

    private fun setRecyclerViewOnScrollListener(
        yelpBusinessSearchService: YelpBusinessSearchService,
        restaurants: MutableList<YelpRestaurants>,
        adapter: RestaurantsAdapter
    ) {
        rvRestaurants.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    // LOAD MORE
                    Log.d(TAG, getString(R.string.msg_failure))
                    requestSearchResult(yelpBusinessSearchService, restaurants, adapter)

                }
            }
        })
    }

    private fun requestSearchResult(
        yelpBusinessSearchService: YelpBusinessSearchService,
        restaurants: MutableList<YelpRestaurants>,
        adapter: RestaurantsAdapter
    ) {
        yelpBusinessSearchService.searchRestaurant(AUTHORIZATION, TERM, LOCATION, LIMIT)
            .enqueue(object : Callback<YelpSearchResult> {
                override fun onResponse(
                    call: Call<YelpSearchResult>,
                    response: Response<YelpSearchResult>
                ) {
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, getString(R.string.msg_did_not_receive_body))
                        return
                    }
                    restaurants.addAll(body.restaurants)
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.d(TAG, getString(R.string.msg_failure), t)
                }
            })
    }

    private fun setRecyclerView(adapter: RestaurantsAdapter) {
        rvRestaurants.adapter = adapter
        adapter.onItemClick = { restaurant ->
            // do something with your item
            Log.d(TAG, restaurant.toString())
            val intent = Intent(this, SecondRestaurantActivity::class.java)
            intent.putExtra(INTENT_EXTRA_RESTAURANT, restaurant)
            startActivity(intent)
        }
        rvRestaurants.layoutManager = LinearLayoutManager(this)
    }


}