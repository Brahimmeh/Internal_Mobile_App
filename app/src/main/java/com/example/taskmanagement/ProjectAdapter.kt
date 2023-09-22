package com.example.taskmanagement

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.User

class ProjectAdapter(
    context: Context,
    resource: Int,
    objects: List<Project>,
    user: User?,
) : ArrayAdapter<Project>(context, resource, objects) {

    var us=user

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.element_project, parent, false)

        val button = rowView.findViewById<Button>(R.id.prjctbtn) // Replace with the actual button ID
        val project = getItem(position)

        if (project != null) {
            button.text = project.name
            val hexColorCode: String
            val drawableLeftResId: Int

            when (project.type) {
                "Vie" -> {
                    hexColorCode = "#228b22"
                    drawableLeftResId = R.drawable.icons8_health_80__1_
                }
                "Non Vie" -> {
                    hexColorCode = "#e9692c"
                    drawableLeftResId = R.drawable.icons8_insurance_agent_50
                }
                "Maladie" -> {
                    hexColorCode = "#8b0000"
                    drawableLeftResId = R.drawable.icons8_nurse_call_80
                }
                "Transport" -> {
                    hexColorCode = "#696969"
                    drawableLeftResId = R.drawable.icons8_vehicle_insurance_50__2_
                }
                else -> {
                    hexColorCode = "#000000" // Default color
                    drawableLeftResId = 0 // No drawable
                }
            }

            val color = Color.parseColor(hexColorCode)
            button.setBackgroundColor(color)

            if (drawableLeftResId != 0) {
                button.setCompoundDrawablesWithIntrinsicBounds(drawableLeftResId, 0, 0, 0)
            }

            button.setOnClickListener {
                val intent = Intent(context, projectDescription::class.java)
                intent.putExtra("crusr",us)
                intent.putExtra("Project", project)
                context.startActivity(intent)
            }
        }

        return rowView
    }

}
