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
import android.widget.Toast
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.Task
import com.example.taskmanagement.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class addproject : AppCompatActivity() {

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
        setContentView(R.layout.activity_addproject)

        supportActionBar?.title = "Ajout d'un Projet "

        val prjname = findViewById<EditText>(R.id.prjtname)
        val prjtype = findViewById<Spinner>(R.id.type)
        val prjtresp = findViewById<Spinner>(R.id.Trespo)
        val addteam = findViewById<Button>(R.id.editteam)
        val budgetpre = findViewById<EditText>(R.id.Bud)


        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")

        //display Users functions
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
        fun fetchUser(userId: String, callback: (User?) -> Unit) {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mamda-taskmanagement-default-rtdb.firebaseio.com/")

            databaseReference.child("Users")
                .child(userId) // Assuming 'userId' is the key for the user in your database
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user: User? = dataSnapshot.toUser()
                        callback(user)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors
                        callback(null) // Callback with null in case of an error
                    }
                })
        }

        //new functions
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


        startDatePrevisionEditText = findViewById(R.id.StartPrev)
        endDatePrevisionEditText = findViewById(R.id.EndPrev)
        startDateEffectiveEditText = findViewById(R.id.StartEff)
        endDateEffectiveEditText = findViewById(R.id.EndEff)

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

        statusSpinner = findViewById(R.id.status)
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



        //call function to display users
        displaydata { userList ->
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userList.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            prjtresp.adapter = adapter

            prjtresp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedUserName = userList[position].name
                    val selectedUser = userList.firstOrNull { it.name == selectedUserName }

                    if (selectedUser != null) {

                        //spinner of type project
                        val adapter = ArrayAdapter.createFromResource(
                            this@addproject,
                            R.array.project_type, // Reference to your string array
                            android.R.layout.simple_spinner_item
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        prjtype.adapter = adapter
                        prjtype.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                // Get the selected item as a string
                                val selectedItem = parent?.getItemAtPosition(position).toString()

                                addteam.setOnClickListener {

                                val prjname = prjname.text.toString()
                                val budg =budgetpre.text.toString()
                                val newPrjtref = databaseReference.child("Projects").push()
                                val prjtId = newPrjtref.key
                                val BudgetReel = calculatePeriodInDays()


                                val Project = Project(prjtId,prjname,selectedItem,null,null,selectedUser
                                    ,dateDebutPrevisionnel,dateFinPrevisionnel,dateDebutEffectif,dateFinEffectif,budg
                                    ,BudgetReel.toString(),selectedStatus)

                                newPrjtref.setValue(Project)
                                    val intent = Intent(this@addproject,addteamprojet::class.java)
                                    intent.putExtra("Project",Project)
                                    startActivity(intent)

                                }


                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // Handle the case where nothing is selected (if needed)
                            }})
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case where nothing is selected (if needed)
                }
            }
        }
    }
}