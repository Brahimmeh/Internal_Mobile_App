package com.example.taskmanagement

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.Task
import com.example.taskmanagement.data.User
import com.example.taskmanagement.ui.tasks.TasksFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date

class addtask : AppCompatActivity() {

    private lateinit var startDatePrevisionEditText: EditText
    private lateinit var endDatePrevisionEditText: EditText
    private lateinit var startDateEffectiveEditText: EditText
    private lateinit var endDateEffectiveEditText: EditText
    private val calendar = Calendar.getInstance()
    private var currentEditText: EditText? = null

    private var dateDebutPrevisionnel: Date? = null
    private var dateFinPrevisionnel: Date? = null
    private var dateDebutEffectif: Date? = null
    private var dateFinEffectif: Date? = null

    private lateinit var statusSpinner: Spinner
    private var selectedStatus: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtask)

        fun navigateToFragment(fragment: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_tasks, fragment) // Replace R.id.fragment_container with your fragment container ID
            transaction.addToBackStack(null) // Optional: Add to the back stack if you want fragment navigation history
            transaction.commit()
        }

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")

        fun displaydata(callback: (List<User>) -> Unit) {
            //display data
            fun DataSnapshot.toUser(): User? {
                return this.getValue(User::class.java)
            }

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

        fun updateDateInView(editText: EditText, calendar: Date) {
            val myFormat = "MM/dd/yyyy" // Change this as needed
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            editText.setText(sdf.format(calendar.time))
        }
        fun showDatePickerDialog(editText: EditText) {
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val selectedDate = calendar.time
                updateDateInView(editText, selectedDate)


            when (editText) {
                startDatePrevisionEditText -> dateDebutPrevisionnel = selectedDate
                endDatePrevisionEditText -> dateFinPrevisionnel = selectedDate
                startDateEffectiveEditText -> dateDebutEffectif = selectedDate
                endDateEffectiveEditText -> dateFinEffectif = selectedDate
            }}

            val datePickerDialog = DatePickerDialog(
                this,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }
        fun disableEditTextInput(editText: EditText) {
            editText.isFocusable = false
            editText.isClickable = true
            editText.isLongClickable = false
            editText.showSoftInputOnFocus = false
        }
        fun setEditTextClickListeners() {
            startDatePrevisionEditText.setOnClickListener {
                currentEditText = startDatePrevisionEditText
                showDatePickerDialog(startDatePrevisionEditText)
            }

            endDatePrevisionEditText.setOnClickListener {
                currentEditText = endDatePrevisionEditText
                showDatePickerDialog(endDatePrevisionEditText)
            }

            startDateEffectiveEditText.setOnClickListener {
                currentEditText = startDateEffectiveEditText
                showDatePickerDialog(startDateEffectiveEditText)
            }

            endDateEffectiveEditText.setOnClickListener {
                currentEditText = endDateEffectiveEditText
                showDatePickerDialog(endDateEffectiveEditText)
            }
        }
        fun calculatePeriodInDays(): Int {
            val startDate = dateDebutEffectif
            val endDate = dateFinEffectif

            if (startDate != null && endDate != null) {
                val difference = endDate.time - startDate.time
                return (difference / (1000 * 60 * 60 * 24)).toInt() // Convert milliseconds to days
            }

            return 0 // Default value if either date is null
        }


        val receivedIntent = intent
        val projet = receivedIntent.getSerializableExtra("Project") as? Project
        val frag= intent.getStringExtra("frag")
        supportActionBar?.title = "Ajout de la Tâche "
        val b = findViewById<TextView>(R.id.prjtnom)
        val userSpinner = findViewById<Spinner>(R.id.userSpinner)
        val name = findViewById<EditText>(R.id.nom)
        val com = findViewById<EditText>(R.id.commen)
        val addbtn = findViewById<Button>(R.id.addtaskbtn)
        val nom = findViewById<TextView>(R.id.prjtnom)
        val budgetpre = findViewById<EditText>(R.id.Budget)
        nom.setText(projet?.name)

        startDatePrevisionEditText = findViewById(R.id.StartDatePrev)
        endDatePrevisionEditText = findViewById(R.id.EndDatePrev)
        startDateEffectiveEditText = findViewById(R.id.StartDateEff)
        endDateEffectiveEditText = findViewById(R.id.EndDateEff)

        startDatePrevisionEditText.setText("Date de début prévisionnel")
        endDatePrevisionEditText.setText("Date de fin prévisionnelle")
        startDateEffectiveEditText.setText("Date de début effectif")
        endDateEffectiveEditText.setText("Date de fin effectif")

        disableEditTextInput(startDatePrevisionEditText)
        disableEditTextInput(endDatePrevisionEditText)
        disableEditTextInput(startDateEffectiveEditText)
        disableEditTextInput(endDateEffectiveEditText)

        setEditTextClickListeners()
        val BudgetReel = calculatePeriodInDays()

        statusSpinner = findViewById(R.id.StatusSpinner)
        val statusList = listOf("Encours", "Bloquée", "Testing", "Done")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        statusSpinner.adapter = adapter

        // Set an OnItemSelectedListener to capture the selected item
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Get the selected item
                selectedStatus = statusList[position]
                Log.i("lpllpl",selectedStatus!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected (optional)
            }
        }



        if(frag=="true")
        {
            b.visibility=View.GONE
        }

        if(projet!=null)
        {

            displaydata { userList ->
                val adapter = ArrayAdapter<String>(this@addtask, android.R.layout.simple_spinner_item, userList.map { it.name })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                userSpinner.adapter = adapter

                userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        // Get the selected user's name
                        val selectedUserName = userList[position].name

                        // Find the user with the selected name to get the ID
                        val selectedUser = userList.firstOrNull { it.name == selectedUserName }

                        if (selectedUser != null) {
                            selectedUser?.name?.let { Log.i("fopl", it) }
                            addbtn.setOnClickListener {

                                val taskname = name.text.toString()
                                val commnt = com.text.toString()
                                val prjname= projet?.name
                                val BudgetReel = calculatePeriodInDays()
                                val bud = budgetpre.text.toString()

                                val newTaskref = databaseReference.child("Tasks").push()
                                val taskId = newTaskref.key
                                val task = selectedStatus?.let { it1 ->
                                    Task(taskId,taskname,
                                        it1,commnt,selectedUser,projet.ProjectID,
                                        dateDebutPrevisionnel,dateFinPrevisionnel,dateDebutEffectif,dateFinEffectif,bud,BudgetReel.toString(),false)
                                }
                                newTaskref.setValue(task)

                                // Update the project's list of tasks if it's not a frag
                                if (projet != null) {
                                    val projectTasks = projet.tasks?.toMutableList() ?: mutableListOf()
                                    if (task != null) {
                                        projectTasks.add(task)
                                    }
                                    projet.tasks=projectTasks

                                    val updatedData = HashMap<String, Any>()
                                    updatedData["tasks"]=projectTasks

                                    databaseReference.child("Projects").child(projet.ProjectID.toString())
                                        .updateChildren(updatedData)
                                        .addOnSuccessListener {
                                            Toast.makeText(this@addtask, "Task added to the project", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this@addtask,list_task::class.java)
                                            intent.putExtra("Project", projet)
                                            startActivity(intent)
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@addtask, "Failed to update project with the new task", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                else{
                                    Toast.makeText(this@addtask,"Creation Reussie",Toast.LENGTH_SHORT).show()
                                    val profilFragment = TasksFragment.newInstance()
                                    navigateToFragment(profilFragment)}
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected (if needed)
                    }
                }
            }
        }

    }
}