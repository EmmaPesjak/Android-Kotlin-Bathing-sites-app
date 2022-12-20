package se.miun.empe2105.dt031g.bathingsites

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Custom class BathingSiteView, contains an image and a increasable counter.
 */
@SuppressLint("SetTextI18n")
class BathingSitesView(
    context: Context, attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

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

        // Set the text of the text field.
        val counterText = resources.getString(R.string.counter_text)
        val nmb = findViewById<TextView>(R.id.bathing_site_nmb)
        nmb.text = "$count $counterText"
        invalidate()
        requestLayout()
    }

    /**
     * Public method fot increasing the count of bathing sites.
     */
    fun increaseCount() {
        // Increase count.
        count += 1
        // Update text.
        val nmb = findViewById<TextView>(R.id.bathing_site_nmb)
        val counterText = resources.getString(R.string.counter_text)
        nmb.text = "$count $counterText"
        invalidate()
        requestLayout()
    }
}
