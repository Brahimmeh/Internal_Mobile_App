package com.example.taskmanagement.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanagement.SharedViewModel
import com.example.taskmanagement.data.Task
import com.example.taskmanagement.databinding.FragmentHomeBinding
import com.example.taskmanagement.ui.profil.ProfilFragment
import com.google.android.play.integrity.internal.h
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //get Current User
        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.observeCurrentUser().observe(viewLifecycleOwner) { user ->

            val welcome: TextView = binding.textWelcome
            welcome.text = "Bienvenue ${user?.name}"

            val des: TextView = binding.desc
            if(user?.isAdmin == true)
            {
                des.setText("""Vous êtes connecté dans l'application de gestion des projets de la Mamda-MCMA en tant qu'un Admin. Dans cette application, vous pouvez effectuer les traitements suivants :

- Créer, modifier et supprimer les différents utilisateurs existants dans la base de données.
- Créer, modifier et supprimer les différents projets avec leurs équipes et leurs tâches.
- Consulter vos informations personnelles et vos tâches.

Nous vous souhaitons une bonne navigation et bon courage dans votre carrière professionnelle.
                """)
            }

            else if(user?.isLeader == true)
            {
                des.setText("""Vous êtes connecté dans l'application de gestion des projets de la Mamda-MCMA en tant qu'un Responsable. Dans cette application, vous pouvez effectuer les traitements suivants :

- Modifier les différents projets avec leurs équipes et leurs tâches.
- Consulter vos informations personnelles et vos tâches.

Nous vous souhaitons une bonne navigation et bon courage dans votre carrière professionnelle.
                """)
            }

            else
            {
                des.setText("""Vous êtes connecté dans l'application de gestion des projets de la Mamda-MCMA en tant qu'un Utilisateur Normal. Dans cette application, vous pouvez effectuer les traitements suivants :

- Modifier les différentes tâches des projets auxquels vous êtes affectés.
- Consulter vos informations personnelles et vos tâches.

Nous vous souhaitons une bonne navigation et bon courage dans votre carrière professionnelle.
                """)
            }

            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/Tasks")

            val currentUserId = user?.userID

            databaseReference.orderByChild("user/userID").equalTo(currentUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val taskList = mutableListOf<Task>()

                        dataSnapshot.children.forEach { taskSnapshot ->
                            val task = taskSnapshot.getValue(Task::class.java)
                            task?.let { taskList.add(it) }
                        }

                        val num= taskList.count()
                        val ts : TextView = binding.NumTeam
                        ts.setText(num.toString())
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors, if any
                    }
                })

        }

//        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
//            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/Projects")
//
//        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                var projectCount = 0 // Initialize the counter
//
//                // Iterate through each project
//                dataSnapshot.children.forEach { projectSnapshot ->
//                    // Check if the project has tasks
//                    if (projectSnapshot.hasChild("tasks")) {
//                        // Iterate through the tasks of the project
//                        val tasksSnapshot = projectSnapshot.child("tasks")
//                        var hasFalseStatusTask = false
//
//                        tasksSnapshot.children.forEach { taskSnapshot ->
//                            val status = taskSnapshot.child("status").getValue(Boolean::class.java)
//
//                            // Check if the task status is false
//                            if (status != null && !status) {
//                                hasFalseStatusTask = true
//                                return@forEach // Exit the loop as soon as a false status task is found
//                            }
//                        }
//
//                        // If a project has at least one false status task, increment the counter
//                        if (hasFalseStatusTask) {
//                            projectCount++
//                        }
//                    }
//                }
//
//                val prjtc: TextView= binding.NumProject
//                prjtc.setText(projectCount.toString())
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle errors, if any
//            }
//        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}