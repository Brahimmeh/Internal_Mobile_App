package com.example.taskmanagement.ui.profil

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
import com.example.taskmanagement.UserAdapter
import com.example.taskmanagement.UserDescription
import com.example.taskmanagement.data.User
import com.example.taskmanagement.databinding.FragmentProfilBinding
import com.example.taskmanagement.signup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
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
        val slideshowViewModel =
            ViewModelProvider(this).get(ProfilhowViewModel::class.java)

        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProfil
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val list = binding.listuser
        val AddBtn : Button = binding.addUser


        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.observeCurrentUser().observe(viewLifecycleOwner) { user ->

            if (user != null) {
                if(user.isAdmin ==true) {
                    // Call displaydata function to fetch user data
                    displaydata { userList ->
                        val adapter = UserAdapter(list.context, R.layout.element_user, userList,user,null,false,null)
                        list.adapter = adapter
                    }

                    AddBtn.setOnClickListener {
                        val intent = Intent(requireContext(), signup::class.java)
                        startActivity(intent)
                    }}


                else{
                    AddBtn.visibility=View.GONE
                    val intent = Intent(context, UserDescription::class.java)

                    val usero = User(user.userID,user.name,
                        user.email,user.password,user.img,
                        user.job_title,user.phone,user.isAdmin,
                        user.isLeader
                    )
                    intent.putExtra("User",usero)
                    intent.putExtra("CUser", usero)
                    context?.startActivity(intent)
                }
            }

        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun displaydata(callback: (List<User>) -> Unit) {
        //display data
        fun DataSnapshot.toUser(): User? {
            return this.getValue(User::class.java)
        }

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")

        databaseReference.child("Users").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data: MutableList<User> = ArrayList()

                dataSnapshot.children.forEach { userSnapshot ->
                    val user: User? = userSnapshot.toUser()
                    user?.let { data.add(it) }
                }

                callback(data)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }
}
