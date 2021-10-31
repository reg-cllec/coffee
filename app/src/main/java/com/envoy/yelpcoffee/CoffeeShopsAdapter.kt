package com.envoy.yelpcoffee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kathleenwang.simpleyelp.R
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_coffee_shop.view.*

class CoffeeShopsAdapter(val context: Context, val coffeeShops: List<YelpCoffeeShops>) :
    RecyclerView.Adapter<CoffeeShopsAdapter.ViewHolder>() {

    var onItemClick: ((YelpCoffeeShops) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_coffee_shop, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val restaurant = coffeeShops[position]
        holder.bind(restaurant)
    }

    override fun getItemCount() = coffeeShops.size
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener { onItemClick?.invoke(coffeeShops[adapterPosition])}
    }
        fun bind(coffeeShop: YelpCoffeeShops) {
            itemView.tvName.text = coffeeShop.name
            itemView.ratingBar.rating = coffeeShop.rating.toFloat()
            itemView.tvNumReviews.text =
                context.getString(R.string.num_reviews, coffeeShop.numReviews)
            itemView.tvAddress.text = coffeeShop.location.address
            itemView.tvCategory.text = coffeeShop.categories[0].title
            itemView.tvDistance.text = coffeeShop.displayDistance()
            itemView.tvPrice.text = coffeeShop.price
            Glide.with(context).load(coffeeShop.imageUrl)
                    .fitCenter()
                    .transform( RoundedCornersTransformation(20, 5))
                    .into(itemView.imageView)

        }

    }
}
