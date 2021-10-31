package com.kathleenwang.simpleyelp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_second_restaurant.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "SecondActivity"
private const val RADIUS = 20
private const val MARGIN = 5


class SecondRestaurantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_restaurant)
        val restaurant = intent.getParcelableExtra<YelpRestaurants>(INTENT_EXTRA_RESTAURANT)
        if (restaurant !== null) {
            secondRatingBar.rating = restaurant.rating.toFloat()
            displayAddress.text = restaurant.location.address
            secondtvName.text = restaurant.name
            val id = restaurant.id
            getData(this, id, restaurant)
        }
    }

    private fun getData(context: Context, id: String, restaurant: YelpRestaurants) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val yelpBusinessService = retrofit.create(YelpBusinessService::class.java)
        yelpBusinessService.getRestaurantById(AUTHORIZATION, id)
            .enqueue(object : Callback<YelpRestaurantResult> {
                override fun onResponse(
                    call: Call<YelpRestaurantResult>,
                    response: Response<YelpRestaurantResult>
                ) {
                    Log.d(TAG, "Response ${response}")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, getString(R.string.msg_did_not_receive_body))
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
                        int = if (int + 1 < 3) { // hacky here to only show the first 3 images
                            int + 1
                        } else {
                            0
                        }
                        Glide.with(context).load(body.photos[int])
                            .fitCenter()
                            .transform(RoundedCornersTransformation(RADIUS, MARGIN))
                            .into(secondtvImage)
                    }
                }

                override fun onFailure(call: Call<YelpRestaurantResult>, t: Throwable) {
                    Log.d(TAG, getString(R.string.msg_failure), t)
                }
            })
    }
}