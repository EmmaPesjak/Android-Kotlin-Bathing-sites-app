package se.miun.empe2105.dt031g.bathingsites

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import java.util.*

/**
 * A simple fragment.
 */
class AddBathingSiteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bathing_site, container, false)
    }

    /**
     * Set the default date to today's date.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        val dateWater: EditText = requireView().findViewById<View>(R.id.date_water) as EditText

        val date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  //notera att detta blir telefonens tidszon
        val formattedDate = dateFormat.format(date)
        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
        dateWater.text = formattedDate.toEditable()
    }
}
