package com.example.cineshare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

data class ListItem(val name: String, val directorName: String, val youtubeLink: String, val avaiable:String)

data class FilmBase(
    val id: String = "",
    val name: String = "",
    val directorName: String = "",
    val available: String = "",
    val link: String = ""
)

class Dashboard: ComponentActivity()  {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)

        val accSetings = findViewById<LinearLayout>(R.id.accSetings)
        val addFilm = findViewById<LinearLayout>(R.id.addFilm)
        val watchFilm = findViewById<LinearLayout>(R.id.watchFilm)
        val searchBtn = findViewById<Button>(R.id.searchBtn)
        database = Firebase.database.reference

        val listView: ListView = findViewById(R.id.container)

        val intent = intent
        var username = ""

        if(intent.hasExtra("Username")){
            username = intent.getStringExtra("Username").toString()
        }

        val query = database.child("films")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val items = mutableListOf<ListItem>()

                for (snapshot in dataSnapshot.children) {
                    val film = snapshot.getValue(FilmBase::class.java)
                    if(film != null){
                        items.add(ListItem(film.name, film.directorName, film.link, film.available));
                    }
                }

                val adapter = CustomAdapter(this@Dashboard, items, username)
                listView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsługa błędów pobierania danych z Firebase
            }
        })

        searchBtn.setOnClickListener {
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val items = mutableListOf<ListItem>()
                    val searchBar = findViewById<EditText>(R.id.searchBar).text.toString()

                    for (snapshot in dataSnapshot.children) {
                        val film = snapshot.getValue(FilmBase::class.java)

                        if (film != null && (searchBar.isBlank() || film.name.contains(searchBar, ignoreCase = true))){
                            items.add(ListItem(film.name, film.directorName, film.link, film.available))
                        }
                    }

                    val adapter = CustomAdapter(this@Dashboard, items, username)
                    listView.adapter = adapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Obsługa błędów pobierania danych z Firebase
                }
            })
        }

        accSetings.setOnClickListener {
            val intent = Intent(this, AccountSetings::class.java)
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