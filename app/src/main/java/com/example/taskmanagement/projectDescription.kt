package com.example.taskmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.Task
import com.example.taskmanagement.data.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date

class projectDescription : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_description)

        fun formatDate(date: Date): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            return dateFormat.format(date)
        }

        val intent = intent
        val proj = intent.getSerializableExtra("Project") as? Project
        val user = intent.getSerializableExtra("crusr") as? User
        supportActionBar?.title = "Description du Projet"

        val text = findViewById<TextView>(R.id.type_prj)
        val text1 = findViewById<TextView>(R.id.nomPrj)
        val text2 = findViewById<TextView>(R.id.respon)
        val dltbn = findViewById<Button>(R.id.dltbutton1)

        val stat = findViewById<TextView>(R.id.stat)
        val budR = findViewById<TextView>(R.id.BudRel)
        val budP = findViewById<TextView>(R.id.BudPre)
        val dateP = findViewById<TextView>(R.id.DatePv)
        val dateF = findViewById<TextView>(R.id.Datef)


        if (user != null) {
            if(user.isAdmin!=true)
                dltbn.visibility=View.GONE
        }


        if (proj != null) {
            text.text=proj.type
            text1.text=proj.name
            text2.text=proj.responsable?.name

            stat.text=proj.status.toString()
            budR.text=proj.BudgetReel.toString()
            budP.text=proj.Budget.toString()
            dateP.text="Du ${proj.DatedebPrev?.let { formatDate(it).toString() }} Au ${proj.DateFinPrev?.let {
                formatDate(
                    it
                ).toString()
            }}"
            dateF.text="Du ${proj.DateDebEff?.let { formatDate(it).toString() }} Au ${proj.DateFinEff?.let {
                formatDate(
                    it
                ).toString()
            }}"
        }

        proj?.BudgetReel?.let { Log.i("jijiji", it) }

        val taskBtn = findViewById<TextView>(R.id.Tasks)
        val teamBtn = findViewById<TextView>(R.id.Team)

        taskBtn.setOnClickListener {
            val intent = Intent(this,list_task::class.java)
            if (proj != null) {
                intent.putExtra("Project", proj)
            }
            intent.putExtra("crusr",user)
            startActivity(intent)
        }


        teamBtn.setOnClickListener {
            val intent= Intent(this, list_user::class.java)
           if (proj != null) {
               if (proj != null) {
                   intent.putExtra("Project", proj)
               }
               intent.putExtra("crusr",user)
            }
            startActivity(intent)
        }

        dltbn.setOnClickListener {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/Projects")
            val projectIDToDelete = proj?.ProjectID

            val projectRefToDelete = databaseReference.child(projectIDToDelete.toString())
            projectRefToDelete.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this@projectDescription, "Projet supprime avec succes", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@projectDescription, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { error ->
                    // Handle the deletion failure
                    println("Erreur: ${error.message}")
                }
        }
}
}