package se.miun.empe2105.dt031g.bathingsites

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Custom class BathingSiteView, contains an image and a increasable counter.
 */
@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("SetTextI18n")
class BathingSitesView(
    context: Context, attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

    private lateinit var appDatabase: AppDatabase

    // https://stackoverflow.com/questions/57788508/static-variables-in-kotlin
    // https://stackoverflow.com/questions/24464663/why-is-my-counter-being-reset-each-time-after-call-tooncreate/24464738#24464738
    // Static counter variable for number of bathing sites.
    companion object {
        var count = 0
    }

    // Inflate on init.
    init {
        inflate(context, R.layout.bathing_site_view, this)

        // Set styleable attributes.
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.BathingSitesView)
            try {
                val nmb = findViewById<TextView>(R.id.bathing_site_nmb)
                nmb.text = attributes.getString(R.styleable.BathingSitesView_nmb)
              } finally {
                attributes.recycle()
            }
        }

        GlobalScope.launch {
            appDatabase = AppDatabase.getDatabase(context)
            count = appDatabase.bathingSiteDao().getAmount()

            // Set the text of the text field.
            showCount()
        }
    }

    /**
     * Public method fot increasing the count of bathing sites.
     */
    fun increaseCount() {
        // Increase count.
        count += 1
        // Update text.
        showCount()
    }

    /**
     * Function for displaying the number of bathing sites in the main activity.
     */
    fun showCount() {

        // Ensure launching on main to avoid crashing when starting the app.
        GlobalScope.launch(Dispatchers.Main){
            val nmb = findViewById<TextView>(R.id.bathing_site_nmb)
            val counterText = resources.getString(R.string.counter_text)
            nmb.text = "$count $counterText"
            invalidate()
            requestLayout()
        }

    }
}
