package fr.reitag.park_share_front_android.ui.home

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.reitag.park_share_front_android.R
import fr.reitag.park_share_front_android.models.Ad
import fr.reitag.park_share_front_android.models.Feedback
import fr.reitag.park_share_front_android.models.Image
import fr.reitag.park_share_front_android.ui.log.LogActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class HomeActivity : AppCompatActivity() {

    private var sortTypeNumber: Int = 1
    private lateinit var adList: ArrayList<Ad>

    private val editTextSearch: EditText by lazy { findViewById<EditText>(R.id.editTextSearch) }
    private val bottomNavigationView: BottomNavigationView by lazy { findViewById(R.id.bottomNavigationView) }
    private val buttonFilter: ImageButton by lazy { findViewById(R.id.buttonFilter) }
    private val buttonOrder: Button by lazy { findViewById(R.id.buttonOrder) }
    private val iconButtonOrder: ImageButton by lazy { findViewById(R.id.iconButtonOrder) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        adList = arrayListOf()

        val idToken = intent.extras?.getString("idToken")
        if (idToken != null) {
            getAds(idToken) { ads ->
                adList.addAll(ads)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentAd, ListFragment.newInstance(adList))
                    .commit()
            }
        }

        buttonOrder.setOnClickListener {
            afficherPopup()
        }

        iconButtonOrder.setOnClickListener {
            afficherPopup()
        }
    }

    private fun afficherPopup() {
        // Créer une vue semi-transparente pour couvrir la disposition principale
        val backgroundView = View(this)
        backgroundView.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey))
        backgroundView.alpha = 0.5f
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        (findViewById<View>(android.R.id.content) as ViewGroup).addView(backgroundView, layoutParams)

        // Créer la vue de popup
        val popupView = layoutInflater.inflate(R.layout.popup_sort, null)
        val radioGroup = popupView.findViewById<RadioGroup>(R.id.radio_group)
        val radioButtonDistance = popupView.findViewById<RadioButton>(R.id.radio_button_distance)
        val radioButtonPrice = popupView.findViewById<RadioButton>(R.id.radio_button_price)
        val radioButtonRating = popupView.findViewById<RadioButton>(R.id.radio_button_rating)
        val rootLayout = popupView.findViewById<View>(R.id.popup_root_layout)

        // Appliquer un arrière-plan semi-transparent à la vue racine
        rootLayout.background = ColorDrawable(ContextCompat.getColor(this, R.color.white))

        // Créer la fenêtre popup
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        // Sélectionner le bouton radio approprié
        when (sortTypeNumber) {
            1 -> radioButtonDistance.isChecked = true
            2 -> radioButtonPrice.isChecked = true
            3 -> radioButtonRating.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Mettre à jour le type de tri en fonction du bouton radio sélectionné
            sortTypeNumber = when (checkedId) {
                R.id.radio_button_distance -> 1
                R.id.radio_button_price -> 2
                R.id.radio_button_rating -> 3
                else -> 1 // Par défaut, tri par distance
            }

            // Trier et mettre à jour la liste d'annonces
            updateAdList()
        }

        // Gérer la fermeture de la popup
        popupWindow.setOnDismissListener {
            // Retirer la vue semi-transparente
            (findViewById<View>(android.R.id.content) as ViewGroup).removeView(backgroundView)
        }

        // Afficher la popup en bas de l'écran
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0)
    }

    private fun updateAdList() {
        when (sortTypeNumber) {
            1 -> {
                val startPoint = Location("locationA")
                startPoint.latitude = 17.372102
                startPoint.longitude = 78.484196

                adList.sortBy {
                    val endPoint = Location("locationB")
                    endPoint.latitude = it.latitude
                    endPoint.longitude = it.longitude
                    startPoint.distanceTo(endPoint).toInt()
                }
            }
            2 -> adList.sortBy { it.hourPrice }
            3 -> {
                val (adsWithFeedback, adsWithoutFeedback) = adList.partition { it.feedbacks.isNotEmpty() }
                val sortedAdsWithFeedback = adsWithFeedback.sortedByDescending {
                    it.feedbacks.mapNotNull { feedback -> feedback.rating }.average()
                }
                val sortedAdsWithoutFeedback = adsWithoutFeedback.sortedBy { it.name }
                adList = (sortedAdsWithFeedback + sortedAdsWithoutFeedback) as ArrayList<Ad>
            }
        }

        // Mettre à jour la liste des annonces dans le fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentAd, ListFragment.newInstance(adList))
            .commit()
    }

    private fun getAds(idToken: String, callback: (MutableList<Ad>) -> Unit) {
        val ads = mutableListOf<Ad>()
        val url = "http://10.0.2.2:8080/api/ads?onlyPublish=true"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $idToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Erreur", e.message.toString())
                callback(ads)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Response", response.message)
                Log.d("Response", response.code.toString())
                response.body?.let { responseBody ->
                    val responseBodyString = responseBody.string()
                    Log.d("Response Body", responseBodyString)
                    val adListType = object : TypeToken<List<Ad>>() {}.type
                    val fetchedAds: List<Ad> = Gson().fromJson(responseBodyString, adListType)
                    fetchImagesAndFeedbacks(fetchedAds, idToken, ads, callback)
                } ?: run {
                    Log.d("Response Body", "Empty Body")
                    callback(ads)
                }
            }
        })
    }

    private fun fetchImagesAndFeedbacks(
        fetchedAds: List<Ad>,
        idToken: String,
        ads: MutableList<Ad>,
        callback: (MutableList<Ad>) -> Unit
    ) {
        val client = OkHttpClient()
        val latch = CountDownLatch(fetchedAds.size * 2)

        for (ad in fetchedAds) {
            fetchImages(ad, idToken, client, latch)
            fetchFeedbacks(ad, idToken, client, ads, callback, latch)
        }
        latch.await()
        callback(ads)
    }

    private fun fetchImages(
        ad: Ad,
        idToken: String,
        client: OkHttpClient,
        latch: CountDownLatch
    ) {
        val imageUrl = "http://10.0.2.2:8080/api/image/ad/${ad.id}"
        val imageRequest = Request.Builder()
            .url(imageUrl)
            .get()
            .addHeader("Authorization", "Bearer $idToken")
            .build()

        client.newCall(imageRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Image Error", e.message.toString())
                latch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Image Response", response.message)
                Log.d("Image Response", response.code.toString())
                response.body?.let {
                    val imageResponseBody = it.string()
                    Log.d("Image Response Body", imageResponseBody)
                    val imageListType = object : TypeToken<List<Image>>() {}.type
                    val fetchedImages: List<Image> = Gson().fromJson(imageResponseBody, imageListType)
                    ad.images = fetchedImages
                    latch.countDown()
                } ?: run {
                    Log.d("Image Response Body", "Empty Body")
                    latch.countDown()
                }
            }
        })
    }

    private fun fetchFeedbacks(
        ad: Ad,
        idToken: String,
        client: OkHttpClient,
        ads: MutableList<Ad>,
        callback: (MutableList<Ad>) -> Unit,
        latch: CountDownLatch
    ) {
        val feedbackUrl = "http://10.0.2.2:8080/api/feedback/ad/${ad.id}"
        val feedbackRequest = Request.Builder()
            .url(feedbackUrl)
            .get()
            .addHeader("Authorization", "Bearer $idToken")
            .build()

        client.newCall(feedbackRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Feedback Error", e.message.toString())
                latch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Feedback Response", response.message)
                Log.d("Feedback Response", response.code.toString())
                response.body?.let {
                    val feedbackResponseBody = it.string()
                    Log.d("Feedback Response Body", feedbackResponseBody)
                    val feedbackListType = object : TypeToken<List<Feedback>>() {}.type
                    val fetchedFeedbacks: List<Feedback> = Gson().fromJson(feedbackResponseBody, feedbackListType)
                    ad.feedbacks = fetchedFeedbacks
                    ads.add(ad)
                    latch.countDown()
                } ?: run {
                    Log.d("Feedback Response Body", "Empty Body")
                    latch.countDown()
                }
            }
        })
    }

}