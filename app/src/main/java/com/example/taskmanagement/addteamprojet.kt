package com.example.taskmanagement

import UserTeamAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.User
import com.google.firebase.database.*

class addteamprojet : AppCompatActivity() {

    private lateinit var userList: MutableList<UserWithSelection>
    private val selectedUsers: MutableList<UserWithSelection> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addteamprojet)

        userList = mutableListOf()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")
        val intent = intent
        val proj = intent.getSerializableExtra("Project") as? Project

        val userListView = findViewById<ListView>(R.id.userListView)
        val namepr = findViewById<TextView>(R.id.prjtname)
        val svbtn = findViewById<Button>(R.id.savbtn)
        val adapter = UserTeamAdapter(this, R.layout.user_element_prjt, userList)
        userListView.adapter = adapter

        if (proj != null) {
            namepr.text = proj.name.toString()
        }

        databaseReference.child("Users").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("FirebaseDebug", "Data changed")
                dataSnapshot.children.forEach { userSnapshot ->
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        val userWithSelection = UserWithSelection(user, false)
                        userList.add(userWithSelection)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })

        supportActionBar?.title = "Ajout d'une équipe"

        svbtn.setOnClickListener {
            // Get the selected users
            val selectedUsers = userList.filter { it.selected }
            val sl = selectedUsers


                    val updatedData = HashMap<String, Any>()
                    if (proj != null) {
                        // Assuming you have a 'team' field in your Project model to store the team members
//                        updatedData["team"] = selectedUsers.map { userWithSelection ->
//                            val user = userWithSelection.user
//                            Log.d("CheckboxDebug", "user: ${userWithSelection.user.name}")
//                            val userMap = HashMap<String, Any>()
//                            userMap["userID"] = user.userID.toString()
//                            userMap["name"] = user.name.toString()
//                            userMap["email"] = user.email.toString()
//                            userMap["admin"] = user.isAdmin.toString()
//                            userMap["job_title"] = user.job_title.toString()
//                            userMap["leader"] = user.isLeader.toString()
//                            userMap["password"] = user.password.toString()
//                            userMap["phone"] = user.phone.toString()
//                            userMap["img"] = user.img.toString()
//                            userMap
//                        }

                        updatedData["team"] = selectedUsers

                        proj.ProjectID?.let { it1 ->
                            databaseReference.child("Projects").child(it1).updateChildren(updatedData)
                                .addOnSuccessListener {

                                    Toast.makeText(this@addteamprojet, "Ajout d'équipe Réussi", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@addteamprojet,list_task::class.java)
                                    intent.putExtra("Project", proj)
                                    startActivity(intent)

                                }
                                .addOnFailureListener {
                                    // Handle failure
                                }
        }
    }
}}}

