package com.example.taskmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.User
import com.example.taskmanagement.ui.profil.ProfilFragment
import com.example.taskmanagement.ui.project.ProjectFragment
import com.example.taskmanagement.ui.tasks.TasksFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class UserDescription : AppCompatActivity() {

    private var currentUsr: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_description)


        fun navigateToFragment(fragment: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_profil, fragment)
            transaction.addToBackStack(null)// Replace R.id.fragment_container with your fragment container ID // Optional: Add to the back stack if you want fragment navigation history
            transaction.commit()
        }

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")
        supportActionBar?.title = "Description d'Utilisateur "

        val intent = intent
        val user = intent.getSerializableExtra("User") as? User
        val ad = intent.getSerializableExtra("CUser")as? User
        val projet = intent.getSerializableExtra("Project")as? Project
        val frm = intent.getBooleanExtra("FromDes",false)

        val editbtn = findViewById<Button>(R.id.edtbutton)
        val savebtn = findViewById<Button>(R.id.savebtn)
        val deletebtn = findViewById<Button>(R.id.dltbutton)
        savebtn.visibility= View.GONE

        if(ad?.isAdmin ==false)
        {
            editbtn.visibility= View.GONE
            deletebtn.visibility= View.GONE
        }

        else{
            editbtn.visibility= View.VISIBLE
            deletebtn.visibility= View.VISIBLE
        }

        if(frm && (ad?.isLeader == true || ad?.isAdmin==true ))
        {
            deletebtn.visibility=View.VISIBLE
            deletebtn.setText("Supprimer du l'équipe")
        }

        //textView
        val name = findViewById<TextView>(R.id.name)
        val email = findViewById<TextView>(R.id.email)
        val JT = findViewById<TextView>(R.id.job)
        val admin = findViewById<TextView>(R.id.admin)
        val lead = findViewById<TextView>(R.id.respo)
        val phone = findViewById<TextView>(R.id.phone)
        name.setText("Le nom complet : "+user?.name)
        email.setText("L'Email : "+user?.email)
        JT.setText("Le poste : "+user?.job_title)
        admin.setText("Admin : "+user?.isAdmin)
        lead.setText("Responsable : "+user?.isLeader)
        phone.setText("Le num Telephone : "+user?.phone)

        //EditText&&Switches
        val ename = findViewById<EditText>(R.id.eName)
        val eemail = findViewById<EditText>(R.id.eEmail)
        val eJT = findViewById<EditText>(R.id.eJob)
        val eadmin = findViewById<Switch>(R.id.eAdmin)
        val elead = findViewById<Switch>(R.id.eRespo)
        val ephone = findViewById<EditText>(R.id.ePhone)
        ename.visibility= View.GONE
        eemail.visibility= View.GONE
        eJT.visibility= View.GONE
        eadmin.visibility= View.GONE
        elead.visibility= View.GONE
        ephone.visibility= View.GONE

        editbtn.setOnClickListener {
            ename.visibility= View.VISIBLE
            eemail.visibility= View.VISIBLE
            eJT.visibility= View.VISIBLE
            eadmin.visibility= View.VISIBLE
            elead.visibility= View.VISIBLE
            ephone.visibility= View.VISIBLE
            savebtn.visibility= View.VISIBLE

            ename.setText(user?.name)
            eemail.setText(user?.email)
            eJT.setText(user?.job_title)
            if(user?.isAdmin==true)
            eadmin.setChecked(true)
            if(user?.isLeader==true)
            elead.setChecked(true)
            ephone.setText(user?.phone)

            name.visibility= View.GONE
            email.visibility= View.GONE
            JT.visibility= View.GONE
            admin.visibility= View.GONE
            lead.visibility= View.GONE
            phone.visibility= View.GONE
            editbtn.visibility= View.GONE
            deletebtn.visibility= View.GONE

            savebtn.setOnClickListener {
                val name_s = ename.text.toString()
                val email_s = eemail.text.toString()
                val job_s = eJT.text.toString()
                val tel_s = ephone.text.toString()
                val admin_s = eadmin.isChecked
                val leader_s = elead.isChecked

                val updatedData = HashMap<String, Any>()
                updatedData["name"] = name_s
                updatedData["email"] = email_s
                updatedData["job_title"] = job_s
                updatedData["phone"] = tel_s
                updatedData["admin"] = admin_s
                updatedData["leader"] = leader_s

                user?.userID?.let { it1 ->
                    databaseReference.child("Users").child(it1).updateChildren(updatedData)
                        .addOnSuccessListener {
                            Toast.makeText(this@UserDescription,"Mise à Jour Reussie",Toast.LENGTH_SHORT).show()
                            val profilFragment = ProfilFragment.newInstance()
                            navigateToFragment(profilFragment)
                        }
                        .addOnFailureListener {}
                }

            }

        }

        deletebtn.setOnClickListener {
            if (!frm) {
                user?.userID?.let { it1 ->
                    databaseReference.child("Users").child(it1).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@UserDescription,
                                "Suppression Reussie",
                                Toast.LENGTH_SHORT
                            ).show()
                            val profilFragment = ProfilFragment.newInstance()
                            navigateToFragment(profilFragment)
                        }
                }
            }
            else
            {
                val projectID = projet?.ProjectID
                val userToRemove = user
                val projectTeamRef = databaseReference.child("Projects").child(projectID.toString()).child("team")

                projectTeamRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (positionSnapshot in dataSnapshot.children) {
                            val position = positionSnapshot.key?.toInt()

                            if (position != null) {
                                val userData = positionSnapshot.child("user").getValue(User::class.java)

                                if (userData != null && userData.userID == userToRemove?.userID) {
                                    // Remove the user from the team by removing the position node
                                    positionSnapshot.ref.removeValue()
                                    break // No need to continue searching
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle any errors here
                    }
                })

            }

                Toast.makeText(this@UserDescription, "Suppression réussie", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@UserDescription,list_project::class.java)
                intent.putExtra("cuser",ad)
                if (projet != null) {
                intent.putExtra("type",projet.type)
                }
                startActivity(intent)

        }

        val photo = findViewById<ImageView>(R.id.pdp)
        photo.setImageResource(R.drawable.manager);
//       Picasso.get()
//         .load(user?.img)
//        .into(photo)


  }


}