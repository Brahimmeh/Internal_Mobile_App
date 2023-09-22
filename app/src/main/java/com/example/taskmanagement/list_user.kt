package com.example.taskmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.Task
import com.example.taskmanagement.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class list_user : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_user)

        val intent = intent
        val projet = intent.getSerializableExtra("Project") as? Project
        val Cruser = intent.getSerializableExtra("crusr") as User
        val adduser = findViewById<Button>(R.id.addUser)

        if(Cruser.isAdmin!=true && Cruser.isLeader!=true) {
            adduser.visibility = View.GONE
        }

        supportActionBar?.title = "Description d'équipe"

        val list = findViewById<View>(R.id.listuser) as ListView
        val data: MutableList<User> = ArrayList()

        if (projet != null) {
            if (projet.team != null) {
                data.addAll(projet.team!!)
            }}

        val adapter = UserAdapter(list.context, R.layout.element_user, data,Cruser,
            projet?.responsable,true,projet)
        list.adapter= adapter

        adduser.setOnClickListener {
            val intent = Intent(this@list_user,UpdateTeamProjectActivity::class.java)
            intent.putExtra("Project",projet)
            intent.putExtra("crusr",Cruser)
            startActivity(intent)
        }
    }

}