package com.example.cineshare

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = ""
)

class LoginActivity : ComponentActivity() {

    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        registerBtn = findViewById(R.id.btnRegister)
        loginBtn = findViewById(R.id.btnLogin)
        database = Firebase.database.reference

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginBtn.setOnClickListener {
            loginAcc()
        }

    }

    private fun dashboard(username: String) {
        val intent = Intent(this, Dashboard::class.java)
        intent.putExtra("Username", username)
        startActivity(intent)
        finish()
    }

    private fun loginAcc(){
        val username = findViewById<EditText>(R.id.editTextUsername).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

        val accRef = database.child("accounts")

        var findAccount = false;
        var blankData = false;

        if(username.isBlank() || password.isBlank()){
            blankData = true;
        }

        accRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (childSnapshot in snapshot.children) {
                    val user = childSnapshot.getValue(User::class.java)

                    if (user != null && user.password.equals(password) && (user.username.equals(username) || user.email.equals(username))){
                        findAccount = true;
                        dashboard(username)
                    }
                }

                if(blankData){
                    showErrorDialog("Proszę o wprowadzanie danych")
                }else if(!findAccount && !blankData){
                    showErrorDialog("Takie konto nie istnieje lub wprowadzono błedne dane")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Błąd logowania")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}