package fr.reitag.park_share_front_android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Feedback(
    var id: Int,
    var adId: Int,
    var userId: String,
    var rating: Int?,
    var description: String,
    @SerializedName("date") // Utilisez cette annotation pour indiquer le nom réel de la propriété dans le JSON
    var dateString: String // Utilisez une chaîne pour stocker la date sous forme de chaîne
) : Parcelable {
    // Ajoutez une fonction pour obtenir un objet LocalDateTime à partir de la chaîne de date
    fun getDate(): LocalDateTime {
        return LocalDateTime.parse(dateString)
    }
}
