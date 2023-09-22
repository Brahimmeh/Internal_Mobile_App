package com.example.taskmanagement

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.Task
import com.example.taskmanagement.data.User
import com.example.taskmanagement.ui.project.ProjectFragment
import com.example.taskmanagement.ui.tasks.TasksFragment
import com.google.android.play.integrity.internal.t
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskDescription : AppCompatActivity() {

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
        setContentView(R.layout.activity_task_description)

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")

        //Functions
        fun getProjectByID(projectID: String, callback: (Project?) -> Unit) {
            val projectReference = databaseReference.child("Projects").child(projectID)

            projectReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val project = dataSnapshot.getValue(Project::class.java)
                        callback(project)
                    } else {
                        // Project with the specified ID does not exist
                        callback(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors, such as network issues or database rules violations
                    // Log or display an error message
                    callback(null)
                }
            })
        }
        fun navigateToFragment(fragment: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_tasks, fragment) // Replace R.id.fragment_container with your fragment container ID
            transaction.addToBackStack(null) // Optional: Add to the back stack if you want fragment navigation history
            transaction.commit()
        }

        fun navigateToFragmentProj(fragment: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_project, fragment) // Replace R.id.fragment_container with your fragment container ID
            transaction.addToBackStack(null) // Optional: Add to the back stack if you want fragment navigation history
            transaction.commit()
        }
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
        fun DataSnapshot.toUser(): User? {
            return this.getValue(User::class.java)
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

        // Receive task
        val intent = intent
        val task = intent.getSerializableExtra("Task") as? Task
        val receivedIntent = intent
        var projet = receivedIntent.getSerializableExtra("Project") as? Project
        val user = receivedIntent.getSerializableExtra("crusr") as? User
        val prjtn = findViewById<TextView>(R.id.prjtname)

//        if(projet==null)
//        {
//            getProjectByID(task?.project.toString()) { project ->
//                if (project != null) {
//                    projet=project
//                    prjtn.setText("Le Projet : ${project?.name}")
//                } else {
//                    // Handle the case where the project doesn't exist
//                }
//            }
//        }

        prjtn.setText("Le Projet : ${projet?.name}")
//        val lista = receivedIntent.getBooleanExtra("Fromlst",false)

        if (task != null) {
            supportActionBar?.title = "Description de la Tâche "
        }

        // Components
        val name = findViewById<TextView>(R.id.name)
        val status = findViewById<TextView>(R.id.status)
        val comment = findViewById<TextView>(R.id.comments)
        val usr = findViewById<TextView>(R.id.user)
        val DateDebutPrev = findViewById<TextView>(R.id.DateDebPre)
        val DateFinPrev = findViewById<TextView>(R.id.DatefinPre)
        val DateDebutEff = findViewById<TextView>(R.id.DateDebEff)
        val DateFineEff = findViewById<TextView>(R.id.DatefinEff)
        val BudgetReel = findViewById<TextView>(R.id.BudgerReel)
        val BudgetPrev = findViewById<TextView>(R.id.BudgetPrev)
        val save = findViewById<Button>(R.id.savebtn)
        val delete = findViewById<Button>(R.id.dletbutton)
        val editb = findViewById<Button>(R.id.edtbtn)
        startDatePrevisionEditText = findViewById(R.id.eDateDebPre)
        endDatePrevisionEditText = findViewById(R.id.eDatefinPre)
        startDateEffectiveEditText = findViewById(R.id.eDateDebEff)
        endDateEffectiveEditText = findViewById(R.id.eDateFinEff)
        statusSpinner = findViewById(R.id.Statuspinner)
        val ebudget = findViewById<EditText>(R.id.eBudgetPrev)

        //hiding edit Buttons
        val ename = findViewById<EditText>(R.id.ename)
        val ecomment = findViewById<EditText>(R.id.ecomment)
        val eusr = findViewById<Spinner>(R.id.euser)
        if (user != null) {
            if (task != null) {
                if(user.isAdmin!=true && user.isLeader!=true)
                    if(task.user?.userID != user.userID) {
                        editb.visibility=View.GONE
                    }
            }
        }
        ename.visibility = View.GONE
        ecomment.visibility = View.GONE
        eusr.visibility = View.GONE
        save.visibility = View.GONE
        statusSpinner.visibility = View.GONE
        startDatePrevisionEditText.visibility = View.GONE
        endDatePrevisionEditText.visibility = View.GONE
        startDateEffectiveEditText.visibility = View.GONE
        endDateEffectiveEditText.visibility = View.GONE
        ebudget.visibility = View.GONE


        if (user != null) {
            if(user.isAdmin!=true && user.isLeader!=true) {
                delete.visibility = View.GONE
            }
        }

        if (task != null) {
            name.text = "Le nom de la tache :${ task.name }"
            comment.text = task.comment
            usr.text = "Affectée à : ${task.user?.name ?: "" }"
            status.text = "Le status de le tache : ${task.status}"
            DateDebutPrev.text = "Debut : ${ task.DatedebPrev.toString() }"
            DateFinPrev.text = "Fin: ${ task.DateFinPrev.toString() }"
            DateDebutEff.text = "Debut: ${ task.DateDebEff.toString() }"
            DateFineEff.text = "Fin: ${ task.DateFinEff.toString() }"
            BudgetReel.text = "Budger Reel: ${ task.BudgetReel }"
            BudgetPrev.text = "Budget Previsionnel: ${ task.Budget }"

        }

        if(user!=null) {
            editb.setOnClickListener {

                if(user.isAdmin==true || user.isLeader==true)
                {

                    name.visibility=View.GONE
                    status.visibility = View.GONE
                    comment.visibility = View.GONE
                    editb.visibility = View.GONE
                    delete.visibility=View.GONE
                    usr.visibility=View.GONE
                    DateDebutPrev.visibility=View.GONE
                    DateFinPrev.visibility=View.GONE
                    DateDebutEff.visibility=View.GONE
                    DateFineEff.visibility=View.GONE
                    BudgetReel.visibility=View.GONE
                    BudgetPrev.visibility=View.GONE

                    ename.visibility = View.VISIBLE
                    ecomment.visibility = View.VISIBLE
                    eusr.visibility = View.VISIBLE
                    save.visibility = View.VISIBLE
                    statusSpinner.visibility = View.VISIBLE
                    startDatePrevisionEditText.visibility = View.VISIBLE
                    endDatePrevisionEditText.visibility = View.VISIBLE
                    startDateEffectiveEditText.visibility = View.VISIBLE
                    endDateEffectiveEditText.visibility = View.VISIBLE
                    ecomment.visibility = View.VISIBLE
                    save.visibility = View.VISIBLE
                    ebudget.visibility = View.VISIBLE

                    disableEditTextInput(startDatePrevisionEditText)
                    disableEditTextInput(endDatePrevisionEditText)
                    disableEditTextInput(startDateEffectiveEditText)
                    disableEditTextInput(endDateEffectiveEditText)

                    setEditTextClickListeners()

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


                }

                else{
                    status.visibility = View.GONE
                    comment.visibility = View.GONE
                    editb.visibility = View.GONE

                    statusSpinner.visibility = View.VISIBLE
                    ecomment.visibility = View.VISIBLE
                    save.visibility = View.VISIBLE

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

                }

                if (task != null) {
                    ename.setText(task.name)
                    ecomment.setText(task.comment)
                    startDatePrevisionEditText.setText(task.DatedebPrev.toString())
                    endDatePrevisionEditText.setText(task.DateFinPrev.toString())
                    startDateEffectiveEditText.setText(task.DateDebEff.toString())
                    endDateEffectiveEditText.setText(task.DateFinEff.toString())
                    ebudget.setText(task.Budget)




                    if(user.isAdmin!=true && user.isLeader!=true)
                    {
                        save.setOnClickListener {
                            val commnt = ecomment.text.toString()
                            task.status=selectedStatus!!
                            val existingTaskId = task.taskID

                            val existingTaskRef =
                                existingTaskId?.let { it1 -> databaseReference.child("Tasks").child(it1) }
                            if (existingTaskRef != null) {
                                existingTaskRef.child("name").setValue(task.name)
                                existingTaskRef.child("comment").setValue(commnt)
                                existingTaskRef.child("status").setValue(selectedStatus)
                                existingTaskRef.child("user").setValue(task.user)
                                existingTaskRef.child("project").setValue(projet?.ProjectID)

                                existingTaskRef.child("dateDebEff").setValue(task.DateDebEff)
                                existingTaskRef.child("dateFinEff").setValue(task.DateFinEff)
                                existingTaskRef.child("dateFinPrev").setValue(task.DateFinPrev)
                                existingTaskRef.child("datedebPrev").setValue(task.DatedebPrev)

                                existingTaskRef.child("budget").setValue(task.Budget)
                                existingTaskRef.child("budgetReel").setValue(task.BudgetReel)
                                existingTaskRef.child("isdeleted").setValue(false)


                            }

                            val projectRef = databaseReference.child("Projects").child(projet?.ProjectID.toString()).child("tasks")
                            val position = projet?.tasks?.indexOfFirst { it.taskID == task.taskID }
                            task.comment=commnt
                            task.status=status.text.toString()

                            if (position != -1) {
                                projectRef.child(position.toString()).setValue(task)
                            }

                            Toast.makeText(this@TaskDescription, "Mise à jour réussie", Toast.LENGTH_SHORT).show()
                            val taskFragment = TasksFragment.newInstance()
                            navigateToFragment(taskFragment)
                        }

                    }

                    else{
                        displaydata { userList ->
                            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userList.map { it.name })
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            eusr.adapter = adapter

                            // Define selectedUser outside of the onItemSelected method
                            var selectedUser: User? = null

                            eusr.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    // Get the selected user's name
                                    selectedUser = userList[position]
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    // Handle the case where nothing is selected (if needed)
                                }
                            }

                            save.setOnClickListener {
                                val taskname = ename.text.toString()
                                val commnt = ecomment.text.toString()
                                val budpre = ebudget.text.toString()
                                val BudgetR = calculatePeriodInDays()
                                BudgetReel.text = "Budger Reel: ${ BudgetR }"


                                // Assuming you have the existingTask and its taskID
                                val existingTaskId = task.taskID

                                if (existingTaskId != null && selectedUser != null) {
                                    val existingTaskRef = databaseReference.child("Tasks").child(existingTaskId)

                                    existingTaskRef.child("name").setValue(taskname)
                                    existingTaskRef.child("comment").setValue(commnt)
                                    existingTaskRef.child("status").setValue(selectedStatus)
                                    existingTaskRef.child("user").setValue(selectedUser)
                                    existingTaskRef.child("project").setValue(projet?.ProjectID)

                                    existingTaskRef.child("dateDebEff").setValue(dateDebutEffectif)
                                    existingTaskRef.child("dateFinEff").setValue(dateFinEffectif)
                                    existingTaskRef.child("dateFinPrev").setValue(dateFinPrevisionnel)
                                    existingTaskRef.child("datedebPrev").setValue(dateDebutPrevisionnel)

                                    existingTaskRef.child("budget").setValue(budpre)
                                    existingTaskRef.child("budgetReel").setValue(BudgetR)
                                    existingTaskRef.child("isdeleted").setValue(false)

                                    val projectRef = databaseReference.child("Projects").child(projet?.ProjectID.toString()).child("tasks")
                                    val position = projet?.tasks?.indexOfFirst { it.taskID == existingTaskId }

                                    task.comment = commnt
                                    task.status = selectedStatus.toString()
                                    task.name = taskname
                                    task.user = selectedUser
                                    task.project = projet?.ProjectID

                                    task.DatedebPrev = dateDebutPrevisionnel
                                    task.DateFinPrev = dateFinPrevisionnel
                                    task.DateDebEff = dateDebutEffectif
                                    task.DateFinEff = dateFinEffectif

                                    Log.i("pooooopopopop",task.DateDebEff.toString())

                                    task.Budget=budpre
                                    task.BudgetReel=BudgetR.toString()
                                    task.isdeleted=false

                                    if (position != -1) {
                                        projectRef.child(position.toString()).setValue(task)
                                    }

                                    Toast.makeText(this@TaskDescription, "Mise à jour réussie", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@TaskDescription, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }

                        }
                    }
                }
            }
        }

        delete.setOnClickListener {

            if (task != null ) {
                val existingTaskRef = task.taskID?.let { it1 ->
                    databaseReference.child("Tasks").child(
                        it1
                    )
                }
                existingTaskRef?.child("name")?.setValue(task.name)
                existingTaskRef?.child("comment")?.setValue(task.comment)
                existingTaskRef?.child("status")?.setValue(task.status)
                existingTaskRef?.child("user")?.setValue(task.user)
                existingTaskRef?.child("project")?.setValue(task.project)

                existingTaskRef?.child("dateDebEff")?.setValue(task.DateDebEff)
                existingTaskRef?.child("dateFinEff")?.setValue(task.DateFinEff)
                existingTaskRef?.child("dateFinPrev")?.setValue(task.DateFinPrev)
                existingTaskRef?.child("datedebPrev")?.setValue(task.DatedebPrev)

                existingTaskRef?.child("budget")?.setValue(task.Budget)
                existingTaskRef?.child("budgetReel")?.setValue(task.BudgetReel)
                existingTaskRef?.child("isdeleted")?.setValue(true)

                val projectRef = databaseReference.child("Projects").child(projet?.ProjectID.toString()).child("tasks")
                val position = projet?.tasks?.indexOfFirst { it.taskID == task.taskID }

                task.isdeleted=true

                if (position != -1) {
                    projectRef.child(position.toString()).setValue(task)
                }

            }

            val tasksList: MutableList<Task> = projet!!.tasks?.toMutableList() ?: mutableListOf()
            val updatedTasksList = tasksList.filter { task1 -> task1.taskID != task?.taskID }.toMutableList()
            projet!!.tasks?.toMutableList()?.clear()
            projet!!.tasks = mutableListOf<Task>()
            projet!!.tasks=updatedTasksList

            val updatedData = HashMap<String, Any>()
            updatedData["tasks"]=updatedTasksList

            databaseReference.child("Projects").child(projet!!.ProjectID.toString())
                .updateChildren(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(this@TaskDescription, "Suppression réussie", Toast.LENGTH_SHORT).show()
                    val taskFragment = TasksFragment.newInstance()
                    navigateToFragment(taskFragment)

                }


        }

    }




}
