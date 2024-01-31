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


data class Account(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = ""
)

class RegistrationActivity : ComponentActivity() {

    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_activity)

        loginBtn = findViewById(R.id.btnLogin)
        registerBtn = findViewById(R.id.btnRegister)
        database = Firebase.database.reference

        loginBtn.setOnClickListener {
            LoginForm()
        }

        registerBtn.setOnClickListener {
            registerAcc()
        }
    }

    private fun LoginForm(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerAcc(){
        val username = findViewById<EditText>(R.id.editTextUsername).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()

        if (username.isNotBlank() && password.isNotBlank() && email.isNotBlank()) {
            database.child("counterAccounts").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val accCounterValue = dataSnapshot.getValue(Long::class.java) ?: 0
                    val newAccountId = (accCounterValue + 1).toString()
                    val newAccount = Account(newAccountId,email,username, password)

                    database.child("counterAccounts").setValue(accCounterValue + 1)
                    database.child("accounts").child(newAccountId).setValue(newAccount)

                    LoginForm()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }else{
            showErrorDialog("Błędne dane")
        }
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Błąd rejestracji")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}