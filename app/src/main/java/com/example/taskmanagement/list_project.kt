package com.example.taskmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ListView
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.Task
import com.example.taskmanagement.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date

class list_project : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_project)

        val type = intent.getStringExtra("type")
        val receivedIntent = intent
        val user = receivedIntent.getSerializableExtra("cuser") as? User

        supportActionBar?.title = "list des Projet $type"
        val cpr = findViewById<TextView>(R.id.text_prj)
        cpr.setText("© 2023 Mamda-Mcma. All rights reserved.")

        val list = findViewById<View>(R.id.listprjt) as ListView
        val addbtn = findViewById<Button>(R.id.addprjt)

        fun retrieveProjectData(projectSnapshot: DataSnapshot): Project? {
            val projectID = projectSnapshot.key // Retrieve the projectID from the node key
            val name = projectSnapshot.child("name").getValue(String::class.java)
            val type = projectSnapshot.child("type").getValue(String::class.java)

            val status = projectSnapshot.child("status").getValue(String::class.java)
            val budgetReel = projectSnapshot.child("budgetReel").getValue(String::class.java)
            val budgetPrev = projectSnapshot.child("budget").getValue(String::class.java)

            val StartDatePrev = projectSnapshot.child("datedebPrev").getValue(Date::class.java)
            val StartDateEff = projectSnapshot.child("dateDebEff").getValue(Date::class.java)
            val EndDateEff = projectSnapshot.child("dateFinEff").getValue(Date::class.java)
            val EndDatePrev = projectSnapshot.child("dateFinPrev").getValue(Date::class.java)

            // Retrieve the team members and tasks based on their positions
            val team = ArrayList<User>()
            val tasks = ArrayList<Task>()

            for (i in 0 until projectSnapshot.child("team").childrenCount) {
                val teamSnapshot = projectSnapshot.child("team").child(i.toString()).child("user")
                val user = teamSnapshot.getValue(User::class.java)
                user?.let { team.add(it) }
            }

            for (i in 0 until projectSnapshot.child("tasks").childrenCount) {
                val taskSnapshot = projectSnapshot.child("tasks").child(i.toString())
                val task = taskSnapshot.getValue(Task::class.java)
                task?.let { tasks.add(it) }
            }

            // Retrieve the responsable user from the "responsable" node
            val responsableSnapshot = projectSnapshot.child("responsable")
            val responsable = responsableSnapshot.getValue(User::class.java)

            return Project(projectID, name, type, team, tasks, responsable, StartDatePrev, EndDatePrev,
                StartDateEff, EndDateEff, budgetPrev, budgetReel, status ) // Create and return the Project object
        }

        fun displaydata(callback: (List<Project>) -> Unit) {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")

            databaseReference.child("Projects").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data: MutableList<Project> = ArrayList()

                    dataSnapshot.children.forEach { projectSnapshot ->
                        val project: Project? = retrieveProjectData(projectSnapshot)
                        if (project != null) {
                            if(project.type==type)
                                project?.let { data.add(it) }
                        }
                    }

                    callback(data)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
        }

        if (user != null) {
            if(user.isAdmin!=true)
            {
                addbtn.visibility=View.GONE
            }
        }

        displaydata { projectList ->
            val adapter = ProjectAdapter(list.context, R.layout.element_project, projectList,user)
            list.adapter= adapter
        }

        addbtn.setOnClickListener {
            val intent = Intent(this, addproject::class.java)
            startActivity(intent)
        }




    }


}