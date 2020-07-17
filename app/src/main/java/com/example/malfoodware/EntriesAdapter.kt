
import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.malfoodware.*
import java.util.*


// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class EntriesAdapter(private val mEntries: List<FoodDiaryEntry>) :
    RecyclerView.Adapter<EntriesAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        var bloodSugar = itemView.findViewById<TextView>(R.id.blood_sugar)
        var foods = itemView.findViewById<TextView>(R.id.foods)
        var time = itemView.findViewById<TextView>(R.id.time)

    }

    interface FoodEntryListListener
    {
        fun showFoodEntryFocussed(entry: FoodDiaryEntry)
        fun hideFoodEntryFocussed()
    }

    private var elementList: MutableMap<Long, View> = mutableMapOf()
    private var clicked: View? = null
    lateinit var activityApp: FoodEntryListListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        if (parent.context is FoodEntryListListener)
        {
            activityApp = parent.context as FoodEntryListListener
        }
        else
        {
            Log.d("LOG", "Could not convert context to FoodEntryListener")
        }
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
        // Set ItemView vars from ViewHolder
        val bloodSugar = holder.bloodSugar
        val foods = holder.foods
        var timeText = holder.time

        // sets the blood sugar text and inserts into item view text
        var bs = ""
        bs += if (entry.bloodSugar == null) "0" else entry.bloodSugar.toString()
        bloodSugar.setText(bs)

        // sets recipe/ingredient names and inserts to viewtext
        var foodsText = ""
        for (recipe in entry.recipes) {
            foodsText += recipe.key.recName + " (${recipe.value.toInt()}g)\n"
        }
        for (ingredient in entry.ingredients) {
            foodsText += ingredient.key.name + " (${ingredient.value.toInt()}g)\n"
        }
        if (foodsText[foodsText.length - 1] == '\n')
            foodsText = foodsText.substring(0, foodsText.length - 1)
        foods.setText(foodsText)

        // sets time and inserts to viewtext
        val cal = Calendar.getInstance()
        cal.timeInMillis = entry.timeMillis
        val hour = cal.get(Calendar.HOUR)
        val min = cal.get(Calendar.MINUTE)
        var timeString = "${hour%12}:"
        timeString += if (min < 10) "0$min" else min
        timeString += if (hour/12 == 0) "am" else "pm"
        timeText.setText(timeString)

        // attaches on click listeners to open focussed fragment
//        val fragment = FoodEntryFocussedFragment(entry)
//        var bundle = Bundle()
//        bundle.putLong(FoodEntryFocussedFragment.BUNDLE_MILLI, entry.timeMillis)
//        fragment.arguments = bundle
        timeText.setOnClickListener(EntryClickListener(entry))
        foods.setOnClickListener(EntryClickListener(entry))
        bloodSugar.setOnClickListener(EntryClickListener(entry))
        elementList[entry.timeMillis] = timeText.parent.parent as View
    }

    inner class EntryClickListener(val entry: FoodDiaryEntry): View.OnClickListener
    {
        @SuppressLint("ResourceAsColor")
        override fun onClick(p0: View?) {
            clearSelection()
            val view = elementList[entry.timeMillis]
            if(clicked != null && clicked!!.equals(view))
            {
                Log.d("LOG", "Food entry clicked twice, detaching")
                view!!.setBackgroundColor(Color.parseColor("#cff1ed"))
                activityApp.hideFoodEntryFocussed()
                clicked = null
            }
            else
            {

                activityApp.hideFoodEntryFocussed()
                Log.d("LOG", "Food entry clicked for first time, attaching")
                clicked = view
                view!!.setBackgroundColor(Color.parseColor("#6bc2b9"))
                activityApp.showFoodEntryFocussed(entry)
            }
        }

    }

    fun clearSelection()
    {
        for(element in elementList)
        {
            element.value.setBackgroundColor(Color.parseColor("#cff1ed"))
        }
    }
}