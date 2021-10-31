package com.envoy.yelpcoffee

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_coffee_shop_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CoffeeShopDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coffee_shop_detail)
        val coffeeShop = intent.getParcelableExtra<YelpCoffeeShops>(INTENT_EXTRA_COFFEE_SHOP)
        if (coffeeShop !== null) {
            secondRatingBar.rating = coffeeShop.rating.toFloat()
            displayAddress.text = coffeeShop.location.address
            secondtvName.text = coffeeShop.name
            val id = coffeeShop.id
            getData(this, id)
        }
    }

    /**
     * fetch coffee shop details and render it on views
     */
    private fun getData(context: Context, id: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val yelpBusinessService = retrofit.create(YelpBusinessService::class.java)
        yelpBusinessService.getCoffeeShopById(AUTHORIZATION, id)
            .enqueue(object : Callback<YelpCoffeeShopDetailResult> {
                override fun onResponse(
                    call: Call<YelpCoffeeShopDetailResult>,
                    response: Response<YelpCoffeeShopDetailResult>
                ) {
                    Log.d(TAG_SECOND_ACTIVITY, "Response ${response}")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG_SECOND_ACTIVITY, getString(R.string.msg_did_not_receive_body))
                        return
                    }
                    fullUrlButton.setOnClickListener {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(body.url)
                        startActivity(openURL)
                    }
                    if (body.is_closed) {
                        secondIsOpen.setTextColor(resources.getColor(R.color.teal_200))
                        secondIsOpen.text = getString(R.string.open)
                    } else {
                        secondIsOpen.setTextColor(resources.getColor(R.color.red_200))
                        secondIsOpen.text = getString(R.string.closed)
                    }
                    secondPhoneNumber.text = body.display_phone
                    var int = 0
                    Glide.with(context).load(body.photos[int])
                        .fitCenter()
                        .transform(RoundedCornersTransformation(RADIUS, MARGIN))
                        .into(secondtvImage)
                    nextImageButton.setOnClickListener {
                        int = if (int + 1 < body.photos.size) {
                            int + 1
                        } else {
                            0  // start over again
                        }
                        Glide.with(context).load(body.photos[int])
                            .fitCenter()
                            .transform(RoundedCornersTransformation(RADIUS, MARGIN))
                            .into(secondtvImage)
                    }
                }

                override fun onFailure(call: Call<YelpCoffeeShopDetailResult>, t: Throwable) {
                    Log.d(TAG_SECOND_ACTIVITY, getString(R.string.msg_failure), t)
                }
            })
    }
}