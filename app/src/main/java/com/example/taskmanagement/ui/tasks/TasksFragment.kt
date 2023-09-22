package com.example.taskmanagement.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanagement.R
import com.example.taskmanagement.SharedViewModel
import com.example.taskmanagement.TaskAdapter
import com.example.taskmanagement.addtask
import com.example.taskmanagement.data.FirebaseTask
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.Task
import com.example.taskmanagement.data.User
import com.example.taskmanagement.databinding.FragmentTasksBinding
import com.example.taskmanagement.ui.profil.ProfilFragment
import com.google.firebase.database.*
import java.util.Date

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ProfilFragment {
            return ProfilFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTask
        val arch : Button = binding.ArchTaskp
        val sharedViewModel =
            ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Observe the current user
        sharedViewModel.observeCurrentUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {

                if(user.isAdmin!=true && user.isLeader!=true) {
                    arch.visibility = View.GONE
                }

                displayTasksForUser(user)
            }
        }

        arch.setOnClickListener {
            sharedViewModel.observeCurrentUser().observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    arch.visibility = View.GONE
                    displayTasksDeleted(user)
                }
            }

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayTasksForUser(user: User) {
        val list = binding.listtask
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")

        databaseReference.child("Projects").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tasks: MutableList<Task> = ArrayList()

                for (projectSnapshot in dataSnapshot.children) {
                    val project = projectSnapshot.getValue(Project::class.java)

                    // Ensure the project is not null and contains tasks
                    if (project != null && !project.tasks.isNullOrEmpty()) {
                        // Filter tasks based on the user
                        val projectTasks = project.tasks!!.filter { task ->
                            (task.user?.userID == user.userID || user.isAdmin == true || user.isLeader == true)  &&
                                    task.isdeleted == false
                        }

                        tasks.addAll(projectTasks)
                    }
                }

                // Display the tasks for the user
                val adapter = TaskAdapter(
                    list.context,
                    R.layout.element_task,
                    tasks,
                    null,
                    user,
                    false
                )
                list.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

    private fun displayTasksDeleted(user: User) {
        val list = binding.listtask
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")

        databaseReference.child("Tasks").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tasks: MutableList<Task> = ArrayList()

                for (taskSnapshot in dataSnapshot.children) {
                    val firebaseTask = taskSnapshot.getValue(FirebaseTask::class.java)

                    // Ensure the task is not null
                    if (firebaseTask != null && firebaseTask.isdeleted == true) {
                        // Convert budgetReel to String if it's not already
                        val budgetReel = when (firebaseTask.budgetReel) {
                            is Long -> (firebaseTask.budgetReel as Long).toString()
                            is String -> firebaseTask.budgetReel as String
                            else -> ""
                        }

                        // Create a new Task object with the converted budgetReel
                        val task = Task(
                            firebaseTask.taskID,
                            firebaseTask.name,
                            firebaseTask.status,
                            firebaseTask.comment,
                            firebaseTask.user,
                            firebaseTask.project,
                            firebaseTask.DatedebPrev,
                            firebaseTask.DateFinPrev,
                            firebaseTask.DateDebEff,
                            firebaseTask.DateFinEff,
                            firebaseTask.Budget,
                            budgetReel, // Set the converted budgetReel
                            firebaseTask.isdeleted
                        )

                        tasks.add(task)
                    }
                }

                // Display the tasks for the user
                val adapter = TaskAdapter(
                    list.context,
                    R.layout.element_task,
                    tasks,
                    null,
                    user,false
                )
                list.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }




}
