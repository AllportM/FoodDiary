
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.malfoodware.FoodDiaryEntry
import com.example.malfoodware.R
import java.util.*


// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class FoodEntriesAdapter(private val mEntries: List<FoodDiaryEntry>) : RecyclerView.Adapter<FoodEntriesAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        var bloodSugar = itemView.findViewById<TextView>(R.id.blood_sugar)
        var foods = itemView.findViewById<TextView>(R.id.foods)
        var time = itemView.findViewById<TextView>(R.id.time)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.diary_entry_row, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return mEntries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val entry: FoodDiaryEntry = mEntries.get(position)
        // Set item views based on your views and data model
        val textView = holder.bloodSugar
        var bs = if (entry.bloodSugar == null) "0" else entry.bloodSugar.toString()
        textView.setText(bs)
        var foodsText = ""
        for (recipe in entry.recipes) {
            foodsText += recipe.key.recName + "\n"
        }
        for (ingredient in entry.ingredients) {
            foodsText += ingredient.key.name + "\n"
        }
        if (foodsText[foodsText.length - 1] == '\n')
            foodsText = foodsText.substring(0, foodsText.length - 1)
        val foods = holder.foods
        foods.setText(foodsText)
        var timeText = holder.time
        val cal = Calendar.getInstance()
        cal.timeInMillis = entry.timeMillis
        val hourString =
            if (cal.get(Calendar.HOUR) < 10) "0" + cal.get(Calendar.HOUR).toString()
            else cal.get(Calendar.HOUR).toString()
        val minuteString =
            if (cal.get(Calendar.MINUTE) < 10) "0" + cal.get(Calendar.MINUTE).toString()
            else cal.get(Calendar.MINUTE).toString()
        val timeString = hourString + ":" + minuteString
        timeText.setText(timeString)
    }
}