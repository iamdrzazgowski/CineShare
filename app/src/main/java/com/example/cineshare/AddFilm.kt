package com.example.cineshare

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

data class FilmAdd(
    val id: String = "",
    val name: String = "",
    val directorName: String = "",
    val available: String = "",
    val link: String = ""
)

class AddFilm : ComponentActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_film_activity)

        val accSetings = findViewById<LinearLayout>(R.id.accSetings)
        val watchFilm = findViewById<LinearLayout>(R.id.watchFilm)
        val home = findViewById<LinearLayout>(R.id.home)
        database = Firebase.database.reference

        val addButton = findViewById<Button>(R.id.addFilmDB)

        val intent = intent
        var username = ""

        if(intent.hasExtra("Username")){
            username = intent.getStringExtra("Username").toString()
        }

        addButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.filmName).text.toString()
            val directonName = findViewById<EditText>(R.id.filmDirector).text.toString()
            val available = findViewById<EditText>(R.id.availableFilms).text.toString()
            val link = findViewById<EditText>(R.id.filmLink).text.toString()

            if(name.isNotBlank() && directonName.isNotBlank() && available.isNotBlank() && link.isNotBlank()){
                database.child("counterFilms").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(isNumber(available)) {
                            if (isYouTubeLink(link)) {
                                val filmCounterValue = dataSnapshot.getValue(Long::class.java) ?: 0
                                val newFilmId = (filmCounterValue + 1).toString()
                                val newFilm =
                                    FilmAdd(newFilmId, name, directonName, available, link)

                                database.child("counterFilms").setValue(filmCounterValue + 1)
                                database.child("films").child(newFilmId).setValue(newFilm)

                                showSuccessDialog("Pomyślnie dodano film/serial do bazy danych")
                            } else {
                                showErrorDialog(
                                    "Podano niepoprawny link to odtwarzacza, proszę podać pełny link do youtube",
                                    "Niepoprawny Link"
                                )
                            }
                        }else{
                            showErrorDialog("Pole available powinno wawierać wartość liczbową np. 3","Niepoprawna wartość")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }else{
                showErrorDialog("Proszę usupełnić wszystkie pola", "Problem z dodaniem filmu do bazy danych")
            }

        }

        accSetings.setOnClickListener {
            val intent = Intent(this, AccountSetings::class.java)
            intent.putExtra("Username", username)
            startActivity(intent)
            finish()
        }

        home.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            intent.putExtra("Username", username)
            startActivity(intent)
            finish()
        }

        watchFilm.setOnClickListener {
            val intent = Intent(this, WatchFilm::class.java)
            intent.putExtra("Username", username)
            startActivity(intent)
            finish()
        }

    }

    private fun showErrorDialog(message: String, title: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pomyślnie dodano")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun isYouTubeLink(link: String): Boolean {
        val youtubeRegex = ("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$").toRegex()
        return link.matches(youtubeRegex)
    }

    fun isNumber(input: String): Boolean {
        return try {
            input.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}