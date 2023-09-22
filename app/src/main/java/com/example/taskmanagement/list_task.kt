package com.example.taskmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.Task
import com.example.taskmanagement.data.User

class list_task : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_task)

        supportActionBar?.title = "List des taches du Projet"

        val receivedIntent = intent
        val projet = receivedIntent.getSerializableExtra("Project") as? Project
        val us = intent.getSerializableExtra("crusr") as? User
        val Froml = intent.getSerializableExtra("Fromlst")
        val list = findViewById<View>(R.id.listtask) as ListView
        val btnsv= findViewById<Button>(R.id.svprj)
        val addbtn = findViewById<Button>(R.id.addTaskl)

        val data: MutableList<Task> = ArrayList()
        if (projet != null) {
                    if (projet.tasks != null) {
                        data.addAll(projet.tasks!!)
        }}

        if (Froml==false) {
            val data: MutableList<Task> = ArrayList()

            projet?.tasks?.let { tasks ->
                us?.let { user ->
                    if (!user.isAdmin!! && !user.isLeader!!) {
                        val filteredTasks = tasks.filter { task ->
                            task.user?.userID == user.userID
                        }
                        data.addAll(filteredTasks)
                    }
                }
            }
        }


        val adapter = TaskAdapter(list.context, R.layout.element_task, data, projet, us,true)
        list.adapter= adapter

        if (us != null) {
            if(us.isAdmin!=true && us.isLeader!=true)
            {
                addbtn.visibility=View.GONE
            }
        }

        addbtn.setOnClickListener {
            val intent = Intent(this@list_task,addtask::class.java)
            intent.putExtra("Project", projet)
            startActivity(intent)
        }

        btnsv.setOnClickListener {
            Toast.makeText(this@list_task,"Modification du Projet reussite",Toast.LENGTH_SHORT).show()
            val intent = Intent(this@list_task,MainActivity::class.java)
            startActivity(intent)
        }



    }

}