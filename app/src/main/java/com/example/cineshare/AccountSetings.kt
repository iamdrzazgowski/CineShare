package com.example.cineshare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class AccountSetings: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_setings)

        val addFilm = findViewById<LinearLayout>(R.id.addFilm)
        val watchFilm = findViewById<LinearLayout>(R.id.watchFilm)
        val home = findViewById<LinearLayout>(R.id.home)

        val logOut = findViewById<Button>(R.id.logoutButton)
        val usernameText = findViewById<TextView>(R.id.textViewUsername)

        var username = ""
        if(intent.hasExtra("Username")){
            username = intent.getStringExtra("Username").toString()
        }

        usernameText.text = username

        logOut.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.removeExtra("Username")
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

        watchFilm.setOnClickListener {
            val intent = Intent(this, WatchFilm::class.java)
            intent.putExtra("Username", username)
            startActivity(intent)
            finish()
        }

    }
}