package com.envoy.yelpcoffee

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kathleenwang.simpleyelp.R
import kotlinx.android.synthetic.main.activity_coffee_shop_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private var OFFSET = 0
private var TOTAL = -1

class CoffeeShopListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coffee_shop_list)
        OFFSET = 0
        TOTAL = -1
        getCoffeeShops()
    }

    /**
     *fetch coffee shop list from yelp api and render it on view
     */
    private fun getCoffeeShops() {
        val coffeeShops = mutableListOf<YelpCoffeeShops>()
        val adapter = CoffeeShopsAdapter(this, coffeeShops)
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val yelpBusinessSearchService = retrofit.create(YelpBusinessSearchService::class.java)
        requestSearchResult(yelpBusinessSearchService, coffeeShops, adapter)
        setRecyclerView(adapter)
        setRecyclerViewOnScrollListener(yelpBusinessSearchService, coffeeShops, adapter)
    }

    /**
     * set onScrolled listener to perform load more when user scroll up
     */
    private fun setRecyclerViewOnScrollListener(
        yelpBusinessSearchService: YelpBusinessSearchService,
        coffeeShops: MutableList<YelpCoffeeShops>,
        adapter: CoffeeShopsAdapter
    ) {
        rvCoffeeShops.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    // LOAD MORE
                        if (OFFSET < TOTAL) {
                            OFFSET += 10
                            requestSearchResult(yelpBusinessSearchService, coffeeShops, adapter)
                        } else {
                            Toast.makeText(
                                this@CoffeeShopListActivity,
                                getString(R.string.no_more),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        })
    }

    /**
     * call yelp api and update recycler view adapter
     */
    private fun requestSearchResult(
        yelpBusinessSearchService: YelpBusinessSearchService,
        coffeeShops: MutableList<YelpCoffeeShops>,
        adapter: CoffeeShopsAdapter
    ) {
        yelpBusinessSearchService.searchCoffeeShop(AUTHORIZATION, TERM, LOCATION, LIMIT, OFFSET.toString())
            .enqueue(object : Callback<YelpSearchResult> {
                override fun onResponse(
                    call: Call<YelpSearchResult>,
                    response: Response<YelpSearchResult>
                ) {
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG_MAIN_ACTIVITY, getString(R.string.msg_did_not_receive_body))
                        return
                    }
                    coffeeShops.addAll(body.coffeeShops)
                    if(TOTAL == -1) { // init total coffee shop count
                        TOTAL = body.total
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.d(TAG_MAIN_ACTIVITY, getString(R.string.msg_failure), t)
                    Toast.makeText(
                        this@CoffeeShopListActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /**
     * set up single item onclick listener
     */
    private fun setRecyclerView(adapter: CoffeeShopsAdapter) {
        rvCoffeeShops.adapter = adapter
        adapter.onItemClick = { coffeeShop ->
            // do something with your item
            Log.d(TAG_MAIN_ACTIVITY, coffeeShop.toString())
            val intent = Intent(this, CoffeeShopDetailActivity::class.java)
            intent.putExtra(INTENT_EXTRA_COFFEE_SHOP, coffeeShop)
            startActivity(intent)
        }
        rvCoffeeShops.layoutManager = LinearLayoutManager(this)
    }
}
