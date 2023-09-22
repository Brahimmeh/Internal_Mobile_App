package com.example.taskmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanagement.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class login : AppCompatActivity() {


    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()


        val email = findViewById<EditText>(R.id.email)
        val pass = findViewById<EditText>(R.id.pass)
        val loginBtn = findViewById<Button>(R.id.butlogn)

        loginBtn.setOnClickListener { v ->
            val inemail = email.text.toString()
            val passw = pass.text.toString()

            if (inemail.isEmpty() || passw.isEmpty()) {
                Toast.makeText(this@login, "Merci d'entrer vos données", Toast.LENGTH_SHORT).show()
            } else mAuth.signInWithEmailAndPassword(inemail, passw)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@login, "Connexion réussie", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@login, "Échec de la connexion", Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }
}
