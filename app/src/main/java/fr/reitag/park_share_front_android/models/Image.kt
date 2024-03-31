package fr.reitag.park_share_front_android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    var id: Int,
    var adId: Int,
    var imageData: ByteArray
) : Parcelable {
    constructor() : this(0, 0, byteArrayOf())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (id != other.id) return false
        if (adId != other.adId) return false
        if (!imageData.contentEquals(other.imageData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + adId
        result = 31 * result + imageData.contentHashCode()
        return result
    }
}
