package com.example.taskmanagement

import UserTeamAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.User
import com.google.firebase.database.*

class UpdateTeamProjectActivity : AppCompatActivity() {

    private lateinit var userList: MutableList<UserWithSelection>
    private lateinit var proj: Project
    private lateinit var crusr: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_team_project)

        userList = mutableListOf()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")

        val userListView = findViewById<ListView>(R.id.userListView)
        val svbtn = findViewById<Button>(R.id.savbtn)
        val adapter = UserTeamAdapter(this, R.layout.user_element_prjt, userList)
        userListView.adapter = adapter

        // Get the project data from the intent
        val intent = intent
        proj = intent.getSerializableExtra("Project") as Project
        crusr = intent.getSerializableExtra("crusr") as User

        // Populate the ListView with users, checking the ones in the project's team
        databaseReference.child("Users").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { userSnapshot ->
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        val isSelected = isUserSelected(user, proj.team)
                        val userWithSelection = UserWithSelection(user, isSelected)
                        userList.add(userWithSelection)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })

        supportActionBar?.title = "Update Team"

        // Inside the UpdateTeamProjectActivity
        svbtn.setOnClickListener {
            // Get the selected users
            val selectedUsers = userList.filter { it.selected }

            // Update the project's team with the selected users
            proj.team = selectedUsers.map { it.user } as MutableList<User>

            // Update the project's team in Firebase Realtime Database
            val projectRef = databaseReference.child("Projects").child(proj.ProjectID!!)
            val updateData = HashMap<String, Any>()

            // Convert the selected users into the Firebase "team" structure
            val teamMembers = selectedUsers.map { userWithSelection ->
                val userMap = HashMap<String, Any>()
                userMap["selected"] = true
                userMap["user"] = userWithSelection.user
                userMap
            }

            updateData["team"] = teamMembers

            projectRef.updateChildren(updateData)
                .addOnSuccessListener {
                    Toast.makeText(this@UpdateTeamProjectActivity, "Team updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@UpdateTeamProjectActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this@UpdateTeamProjectActivity, "Failed to update team: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun isUserSelected(user: User, team: List<User>?): Boolean {
        return team?.any { it.userID == user.userID } ?: false
    }
}


