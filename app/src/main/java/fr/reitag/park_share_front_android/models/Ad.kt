package fr.reitag.park_share_front_android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ad(
    val id: Int,
    val userId: String,
    val name: String,
    val description: String,
    val hourPrice: Double,
    val latitude: Double,
    val longitude: Double,
    val state: Boolean,
    var images: List<Image> = mutableListOf(),
    var feedbacks: List<Feedback> = mutableListOf(),
    val link: String
) : Parcelable {
    constructor() : this(0, "", "", "", 0.0, 0.0, 0.0, false, mutableListOf(), mutableListOf(), "")
}
