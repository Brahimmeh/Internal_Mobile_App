import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import com.example.taskmanagement.R
import com.example.taskmanagement.UserWithSelection

class UserTeamAdapter(
    context: Context,
    resource: Int,
    private val userList: List<UserWithSelection>
) : ArrayAdapter<UserWithSelection>(context, resource, userList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.user_element_prjt, parent, false)
        val userWithSelection = userList[position]
        val checkBox = rowView.findViewById<CheckBox>(R.id.checkBox)

        // Set user name as text for the list item
        val button = rowView.findViewById<Button>(R.id.userbtn)
        button.text = userWithSelection.user.name

        // Initialize checkbox state based on isSelected
        checkBox.isChecked = userWithSelection.selected

        // Listen to checkbox changes
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            userWithSelection.selected= isChecked
        }

        return rowView
    }
}
