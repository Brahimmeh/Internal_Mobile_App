package com.example.taskmanagement.ui.project

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanagement.R
import com.example.taskmanagement.SharedViewModel
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.User
import com.example.taskmanagement.databinding.FragmentProjectsBinding
import com.example.taskmanagement.list_project
import com.example.taskmanagement.projectDescription
import com.example.taskmanagement.ui.profil.ProfilFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProjectFragment : Fragment() {

    private var _binding: FragmentProjectsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ProjectFragment {
            return ProjectFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(ProjectViewModel::class.java)

        _binding = FragmentProjectsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProject
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        val vie = binding.Vie
        val nonvie = binding.NonVie
        val maladie = binding.Maladie
        val trnsp = binding.Transport


        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.observeCurrentUser().observe(viewLifecycleOwner) { user ->

            if (user != null) {

                vie.setOnClickListener {
                    val intent = Intent(requireActivity(), list_project::class.java)
                    intent.putExtra("cuser",user)
                    intent.putExtra("type","Vie")
                    startActivity(intent)
                }

                nonvie.setOnClickListener {
                    val intent = Intent(requireActivity(), list_project::class.java)
                    intent.putExtra("cuser",user)
                    intent.putExtra("type","Non Vie")
                    startActivity(intent)
                }

                maladie.setOnClickListener {
                    val intent = Intent(requireActivity(), list_project::class.java)
                    intent.putExtra("cuser",user)
                    intent.putExtra("type","Maladie")
                    startActivity(intent)
                }

                trnsp.setOnClickListener {
                    val intent = Intent(requireActivity(), list_project::class.java)
                    intent.putExtra("cuser",user)
                    intent.putExtra("type","Transport")
                    startActivity(intent)
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}