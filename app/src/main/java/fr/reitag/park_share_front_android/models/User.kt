package fr.reitag.park_share_front_android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val role: String,
) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}
