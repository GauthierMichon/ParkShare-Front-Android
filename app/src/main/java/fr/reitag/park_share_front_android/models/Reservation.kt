package fr.reitag.park_share_front_android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Reservation(
    var id: Int,
    var ad: Ad,
    var userId: String,
    var beginDate: LocalDateTime,
    var endDate: LocalDateTime,
    var totalPrice: Double,
    var statusId: Int,
) : Parcelable {
    constructor() : this(0, Ad(), "", LocalDateTime.now(), LocalDateTime.now(), 0.0, 0)
}
