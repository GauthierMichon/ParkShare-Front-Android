package fr.reitag.park_share_front_android.ui.home

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.reitag.park_share_front_android.R
import fr.reitag.park_share_front_android.models.Ad


class AdListAdapter(val context: Context, var items: List<Ad>)
    : RecyclerView.Adapter<AdListAdapter.ViewHolder>() {

    private var finalItems = items

    /* private var finalItems = filterItems(items, searchFilter, sortTypeNumber)

    fun customNotifyDataSetChanged() {
        finalItems = filterItems(items, searchFilter, sortTypeNumber)
        notifyDataSetChanged()
    }

    fun filterItems(items: ArrayList<Recipe>, searchFilter: String, sortTypeNumber: Int): List<Recipe> {
        if (sortTypeNumber == 1) {
            items.sortBy { it.name }
        } else if (sortTypeNumber == 2) {
            items.sortBy { Calories().calculation(it) }
        } else if (sortTypeNumber == 3) {
            items.sortByDescending { it.note }
        } else if (sortTypeNumber == 4) {
            items.sortByDescending { it.id }
        } else {
            items.sortBy { it.id }
        }
        val filteredItems = items.filter { it.name.contains(searchFilter, ignoreCase = true) }
        return filteredItems
    }*/


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_ad,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = finalItems.get(position)

        /*val directory = context.getDir("recipes_images", Context.MODE_PRIVATE)
        val file = File(directory, String.format("%s.jpg", item.images[0]))
        val uri = Uri.fromFile(file)
        // Afficher l'image avec Glide
        println("file : -----------------------------------------------")
        println(uri)*/
        /*holder.itemButtonRecipe?.let {
            Glide.with(context)
                .load(uri)
                .into(it)
        }*/

        val startPoint = Location("locationA")
        startPoint.latitude = 17.372102
        startPoint.longitude = 78.484196

        val endPoint = Location("locationB")
        endPoint.latitude = item.latitude
        endPoint.longitude = item.longitude

        val distanceInMeters = startPoint.distanceTo(endPoint).toInt()

        val distance: String = if (distanceInMeters > 1000) {
            val distanceInKilometers = distanceInMeters / 1000
            String.format("%dkm", distanceInKilometers)
        } else {
            String.format("%dm", distanceInMeters)
        }

        val averageRating = item.feedbacks.mapNotNull { it.rating }.average()
        val formattedRating = if (!averageRating.isNaN()) {
            String.format("%.1f", averageRating)
        } else {
            "N/A" // Remplacez par une valeur par défaut si NaN est détecté
        }
        holder.textViewRating.text = formattedRating
        holder.textViewDistance.text = distance
        holder.textViewAdTitle.text = String.format("%s", item.name)
        holder.textViewPrice.text = String.format("%s €", item.hourPrice)
    }

    override fun getItemCount(): Int {
        return finalItems.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewAdTitle: TextView = view.findViewById(R.id.textViewAdTitle)
        val textViewRating: TextView = view.findViewById(R.id.textViewRating)
        val textViewDistance: TextView = view.findViewById(R.id.textViewDistance)
        val textViewPrice: TextView = view.findViewById(R.id.textViewPrice)
    }


}

