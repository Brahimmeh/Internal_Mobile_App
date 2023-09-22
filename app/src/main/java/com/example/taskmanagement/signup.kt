package com.example.taskmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.example.taskmanagement.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class signup : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar?.title = "Ajout D'un Utilisateur"

        mAuth = FirebaseAuth.getInstance()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        val previousUser = mAuth.currentUser
        val emailp = previousUser?.email.toString()
        val passp = "123456"

        val name = findViewById<EditText>(R.id.nom)
        val email= findViewById<EditText>(R.id.email)
        val pass= findViewById<EditText>(R.id.Password)
        val job= findViewById<EditText>(R.id.job)
        val tel= findViewById<EditText>(R.id.Phone)
        val admin= findViewById<Switch>(R.id.isadmin)
        val leader= findViewById<Switch>(R.id.isleader)
        val addbtn = findViewById<Button>(R.id.addbtn)

        addbtn.setOnClickListener {
            val name_s = name.text.toString()
            val email_s = email.text.toString()
            val pass_S = pass.text.toString()
            val job_s = job.text.toString()
            val tel_s = tel.text.toString()
            val admin_s = admin.isChecked
            val leader_s = leader.isChecked

            if (email_s.isEmpty() || pass_S.isEmpty()) {
                Toast.makeText(this@signup, "Entrer toutes les informations", Toast.LENGTH_SHORT).show()
            }

            else{
                mAuth.createUserWithEmailAndPassword(email_s, pass_S)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign up success
                            val user = mAuth.currentUser
                            val uid = user?.uid
                            if (user != null) {
                                val User = User(user.uid,name_s,email_s,pass_S," ",job_s,tel_s,admin_s,leader_s)
                                if (uid != null) {
                                    databaseReference.child(uid).setValue(User)
                                }

                                Toast.makeText(this, "Creation réussie", Toast.LENGTH_SHORT).show()
                                mAuth.signOut()
                                val intent = Intent(this, login::class.java)
                                startActivity(intent)
                            }}
                            else{
                                // Sign up failed
                                val errorMessage = task.exception?.message ?: "Unknown error occurred"
                                Toast.makeText(this@signup, errorMessage, Toast.LENGTH_SHORT).show()
                            }

            }
        }
    }
}}