package com.example.taskmanagement

import android.graphics.Color
import com.example.taskmanagement.data.Task
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.User

class TaskAdapter(
    context: Context,
    resource: Int,
    objects: List<Task>,
    prjt: Project?,
    user: User?,
    lista: Boolean
) :
    ArrayAdapter<Task>(context, resource, objects) {

    var projet=prjt
    var us=user
    var lst=lista

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.element_task, parent, false)

        val button = rowView.findViewById<Button>(R.id.taskbtn) // Replace with the actual button ID
        val instance = getItem(position) // Set the button text to the corresponding data item
        if (instance != null) {
            button.text=instance.name

            if(instance.status == "Done")
            {
                val hexColorCode ="#2DD831"
                val color = Color.parseColor(hexColorCode)
                button.setBackgroundColor(color)

                val drawableLeftResId = R.drawable.icons8_check_50
                button.setCompoundDrawablesWithIntrinsicBounds(drawableLeftResId, 0, 0, 0)

            }

            else{

                val hexColorCode ="#ff0000"
                val color = Color.parseColor(hexColorCode)
                button.setBackgroundColor(color)

                val drawableLeftResId = R.drawable.icons8_x_50__1_
                button.setCompoundDrawablesWithIntrinsicBounds(drawableLeftResId, 0, 0, 0)

            }
        }

        button.setOnClickListener {
            val intent = Intent(context,TaskDescription::class.java)
            Log.i("test2", projet?.name.toString())
            intent.putExtra("Project", projet)
            intent.putExtra("Fromlst", lst)
            intent.putExtra("Task",instance)
            intent.putExtra("crusr",us)
            context.startActivity(intent)
        }
        return rowView
    }
}
