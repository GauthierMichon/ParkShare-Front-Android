package fr.reitag.park_share_front_android.ui.log

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import fr.reitag.park_share_front_android.R
import fr.reitag.park_share_front_android.ui.home.HomeActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LogActivity : AppCompatActivity() {

    private val editTextEmailLogIn: TextView by lazy { findViewById<TextView>(R.id.editTextEmailLogIn) }
    private val editTextPasswordLogIn: TextView by lazy { findViewById<TextView>(R.id.editTextPasswordLogIn) }
    private val editTextFirstNameSignUp: TextView by lazy { findViewById<TextView>(R.id.editTextFirstNameSignUp) }
    private val editTextLastNameSignUp: TextView by lazy { findViewById<TextView>(R.id.editTextLastNameSignUp) }
    private val editTextEmailSignUp: TextView by lazy { findViewById<TextView>(R.id.editTextEmailSignUp) }
    private val editTextPasswordSignUp: TextView by lazy { findViewById<TextView>(R.id.editTextPasswordSignUp) }
    private val editTextConfirmPasswordSignUp: TextView by lazy { findViewById<TextView>(R.id.editTextConfirmPasswordSignUp) }

    private val buttonLogIn: android.widget.Button by lazy { findViewById(R.id.buttonLogIn) }
    private val buttonSignUp: android.widget.Button by lazy { findViewById(R.id.buttonSignUp) }
    private val buttonLogInSwitch: android.widget.Button by lazy { findViewById(R.id.signInSwitch) }
    private val buttonSignUpSwitch: android.widget.Button by lazy { findViewById(R.id.signUpSwitch) }

    private val underlineSignIn: View by lazy { findViewById(R.id.underlineSignIn) }
    private val underlineSignUp: View by lazy { findViewById(R.id.underlineSignUp) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        buttonLogInSwitch.setOnClickListener {
            editTextEmailLogIn.visibility = View.VISIBLE
            editTextPasswordLogIn.visibility = View.VISIBLE
            buttonLogIn.visibility = View.VISIBLE
            underlineSignIn.visibility = View.VISIBLE

            editTextFirstNameSignUp.visibility = View.INVISIBLE
            editTextLastNameSignUp.visibility = View.INVISIBLE
            editTextEmailSignUp.visibility = View.INVISIBLE
            editTextPasswordSignUp.visibility = View.INVISIBLE
            editTextConfirmPasswordSignUp.visibility = View.INVISIBLE
            buttonSignUp.visibility = View.INVISIBLE
            underlineSignUp.visibility = View.INVISIBLE
        }

        buttonSignUpSwitch.setOnClickListener {
            editTextEmailLogIn.visibility = View.INVISIBLE
            editTextPasswordLogIn.visibility = View.INVISIBLE
            buttonLogIn.visibility = View.INVISIBLE
            underlineSignIn.visibility = View.INVISIBLE

            editTextFirstNameSignUp.visibility = View.VISIBLE
            editTextLastNameSignUp.visibility = View.VISIBLE
            editTextEmailSignUp.visibility = View.VISIBLE
            editTextPasswordSignUp.visibility = View.VISIBLE
            editTextConfirmPasswordSignUp.visibility = View.VISIBLE
            buttonSignUp.visibility = View.VISIBLE
            underlineSignUp.visibility = View.VISIBLE
        }

        buttonLogIn.setOnClickListener {
            val email = editTextEmailLogIn.text.toString()
            val password = editTextPasswordLogIn.text.toString()

            // Vérification que les champs ne sont pas vides
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                // Appel de la fonction pour l'authentification avec email et mot de passe
                authenticate(email, password)
            }
        }

        // Ajout du click listener pour le bouton d'inscription
        buttonSignUp.setOnClickListener {
            val firstName = editTextFirstNameSignUp.text.toString()
            val lastName = editTextLastNameSignUp.text.toString()
            val email = editTextEmailSignUp.text.toString()
            val password = editTextPasswordSignUp.text.toString()
            val confirmPassword = editTextConfirmPasswordSignUp.text.toString()

            // Vérification que les champs ne sont pas vides et que le mot de passe correspond à la confirmation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Veuillez saisir une adresse email valide", Toast.LENGTH_SHORT).show()
            } else {
                // Appel de la fonction pour l'enregistrement avec prénom, nom, email et mot de passe
                register(firstName, lastName, email, password)
            }
        }
    }

    private fun authenticate(email: String, password: String) {
        val url = "http://10.0.2.2:8080/api/user/authentication"
        val client = OkHttpClient()

        // Créer un objet JSON pour les données à envoyer
        val json = JSONObject()
        json.put("email", email)
        json.put("password", password)
        json.put("returnSecureToken", true)

        // Créer le corps de la requête avec les données JSON
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Erreur", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Response", response.message)
                Log.d("Response", response.code.toString())
                response.body?.let { responseBody ->
                    val jsonString = responseBody.string()
                    val jsonObject = JSONObject(jsonString)
                    val idToken = jsonObject.getString("idToken")

                    val intentHome: Intent = Intent(this@LogActivity, HomeActivity::class.java)
                    intentHome.putExtra("idToken", idToken)
                    startActivity(intentHome)
                    finish()
                }
            }
        })
    }


    private fun register(firstName: String, lastName: String, email: String, password: String) {
        val url = "http://10.0.2.2:8080/api/user/register"
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("firstname", firstName)
        json.put("lastname", lastName)
        json.put("email", email)
        json.put("password", password)
        json.put("roleId", 1)

        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Erreur", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Response", response.message)
                Log.d("Response", response.code.toString())
                response.body?.let { Log.d("Response", it.string()) }
            }
        })
    }
}