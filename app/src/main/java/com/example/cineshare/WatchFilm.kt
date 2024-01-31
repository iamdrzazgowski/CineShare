package com.example.cineshare

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity

class WatchFilm : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watch_film_activity)

        val accSetings = findViewById<LinearLayout>(R.id.accSetings)
        val addFilm = findViewById<LinearLayout>(R.id.addFilm)
        val home = findViewById<LinearLayout>(R.id.home)
        val watchBtn = findViewById<Button>(R.id.watchBtn)

        val intent = intent
        var username = ""
        var code = ""
        var youtubeLink = ""

        if(intent.hasExtra("Username")){
            username = intent.getStringExtra("Username").toString()
        }

        if(intent.hasExtra("Code") && intent.hasExtra("YoutubeLink")){
            code = intent.getStringExtra("Code").toString()
            youtubeLink = intent.getStringExtra("YoutubeLink").toString()
        }

        watchBtn.setOnClickListener {
            val codeText = findViewById<EditText>(R.id.codeBar).text.toString()

            if(codeText == code && youtubeLink.isNotBlank()){
                openYouTubeApp(youtubeLink)
            }else{
                showErrorDialog("Sprawdź czy wpisałeś pprawnie kod.")
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

        addFilm.setOnClickListener {
            val intent = Intent(this, AddFilm::class.java)
            intent.putExtra("Username", username)
            startActivity(intent)
            finish()
        }

    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Błąd przy wprowadzaniu kodu")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun openYouTubeApp(youtubeLink: String) {
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
        if (intentApp.resolveActivity(packageManager) != null) {
            startActivity(intentApp)
        } else {
            val intentWeb = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            startActivity(intentWeb)
        }
    }
}