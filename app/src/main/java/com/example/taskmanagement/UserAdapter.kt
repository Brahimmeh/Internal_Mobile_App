package com.example.taskmanagement

import android.graphics.Color
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.lifecycle.LiveData
import com.example.taskmanagement.data.Project
import com.example.taskmanagement.data.User

class UserAdapter(context: Context, resource: Int, objects: List<User>, crU: User?,Respo: User?,
                  Des: Boolean, prjt: Project?) :
    ArrayAdapter<User>(context, resource, objects) {

    val usr=crU
    val res=Respo
    val des=Des
    val pr = prjt

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.element_user, parent, false)
        val button = rowView.findViewById<Button>(R.id.userbtn) // Replace with the actual button ID
        val instance = getItem(position) // Set the button text to the corresponding data item

        if(des==false)
        {
            if (instance != null ) {
                if(instance.isLeader==true)
                {
                    button.text = "${instance.name} (Responsable)"
                    val color = Color.parseColor("#ffe4b5")
                    button.setBackgroundColor(color)
                    button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icons8_member_50, 0, 0, 0)
                }
                else if(instance.isAdmin==true)
                {
                    button.text = "${instance.name} (Admin)"
                    val color = Color.parseColor("#ffe4b5")
                    button.setBackgroundColor(color)
                    button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icons8_member_50, 0, 0, 0)
                }
                else
                    button.text=instance.name

            }
        }

        else
        {
                    button.text=instance?.name
        }


        button.setOnClickListener {
            val intent = Intent(context,UserDescription::class.java)
            intent.putExtra("Project", pr)
            intent.putExtra("FromDes",des)
            intent.putExtra("User",instance)
            intent.putExtra("CUser", usr)
            context.startActivity(intent)
        }
        return rowView
    }
}
