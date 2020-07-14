
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.fragment.app.FragmentManager

import androidx.recyclerview.widget.RecyclerView
import com.example.malfoodware.FoodDiaryEntry
import com.example.malfoodware.FoodEntryFocussedFragment
import com.example.malfoodware.FoodEntryFragmentMain
import com.example.malfoodware.R
import org.w3c.dom.Text
import java.util.*


// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
class FoodEntriesAdapter(private val mEntries: List<FoodDiaryEntry>,
    private val fm: FragmentManager
    ) : RecyclerView.Adapter<FoodEntriesAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        var bloodSugar = itemView.findViewById<TextView>(R.id.blood_sugar)
        var foods = itemView.findViewById<TextView>(R.id.foods)
        var time = itemView.findViewById<TextView>(R.id.time)

    }

    var elementList: MutableMap<Long, MutableList<TextView>> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.diary_entry_row, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return mEntries.size-1
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
        val hourString =
            if (cal.get(Calendar.HOUR) < 10) "0" + cal.get(Calendar.HOUR).toString()
            else cal.get(Calendar.HOUR).toString()
        val minuteString =
            if (cal.get(Calendar.MINUTE) < 10) "0" + cal.get(Calendar.MINUTE).toString()
            else cal.get(Calendar.MINUTE).toString()
        val timeString = "" + hourString + ":" + minuteString
        timeText.setText(timeString)

        // attaches on click listeners to open focussed fragment
        val fragment = FoodEntryFocussedFragment(entry)
        var bundle = Bundle()
        bundle.putLong(FoodEntryFocussedFragment.BUNDLE_MILLI, entry.timeMillis)
        fragment.arguments = bundle
        timeText.setOnClickListener(EntryClickListener(fragment, entry))
        foods.setOnClickListener(EntryClickListener(fragment, entry))
        bloodSugar.setOnClickListener(EntryClickListener(fragment, entry))
        elementList[entry.timeMillis] = mutableListOf(timeText, foods, bloodSugar)
    }

    inner class EntryClickListener(val focussedFragment: FoodEntryFocussedFragment,
    val entry: FoodDiaryEntry): View.OnClickListener
    {
        override fun onClick(p0: View?) {
            val transaction = fm.beginTransaction()
            var entryView = fm.findFragmentById(R.id.nav_host_fragment)
            // sets colours of all items back to normal
            for (views in elementList)
            {
                for (element in views.value)
                {
                    element.setBackgroundColor(Color.parseColor("#ede9e9"))
                }
            }
            val oldFocussedFrag = fm.findFragmentByTag(FoodEntryFocussedFragment.FRAGMENT_TAG)
            if (oldFocussedFrag != null)
            {
                println("focussedfrag not null")
                var arguments = oldFocussedFrag.arguments
                if (arguments != null &&
                    arguments.getLong(FoodEntryFocussedFragment.BUNDLE_MILLI) == entry.timeMillis)
                {
                    println("focussedfrag matched")
                    transaction.remove(oldFocussedFrag)
                    transaction.replace(R.id.nav_host_fragment, entryView!!, FoodEntryFragmentMain.FRAGMENT_TAG)
                    transaction.commit()
                    fm.popBackStack(FoodEntryFocussedFragment.FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
                else
                {
                    println("focussedfrag not matched")
                    transaction.remove(oldFocussedFrag)
                    transaction.add(focussedFragment, FoodEntryFocussedFragment.FRAGMENT_TAG)
                    transaction.replace(R.id.nav_host_fragment, focussedFragment, FoodEntryFocussedFragment.FRAGMENT_TAG)
                    transaction.add(entryView!!, FoodEntryFragmentMain.FRAGMENT_TAG)
                    transaction.commit()
                    fm.popBackStack(FoodEntryFocussedFragment.FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val entries = elementList[entry.timeMillis]
                    if (entries != null)
                        for (i in 0 until entries.size)
                        {
                            entries[i].setBackgroundColor(Color.parseColor("#ede9e9"))
                        }
                }
            }
            else {
                println("no old focussed view")
                transaction.add(focussedFragment, FoodEntryFocussedFragment.FRAGMENT_TAG)
                transaction.replace(R.id.nav_host_fragment, focussedFragment, FoodEntryFocussedFragment.FRAGMENT_TAG)
                transaction.add(entryView!!, FoodEntryFragmentMain.FRAGMENT_TAG).addToBackStack(null)
                transaction.commit()
                val entries = elementList[entry.timeMillis]
                if (entries != null)
                    for (i in 0 until entries.size)
                    {
                        entries[i].setBackgroundColor(Color.parseColor("#ede9e9"))
                    }
            }
        }

    }
}