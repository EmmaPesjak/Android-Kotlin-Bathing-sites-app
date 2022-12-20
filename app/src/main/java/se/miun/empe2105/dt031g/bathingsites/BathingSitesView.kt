package se.miun.empe2105.dt031g.bathingsites

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout


@SuppressLint("SetTextI18n")
class BathingSitesView(
    context: Context, attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {


    // gör den static
    // https://stackoverflow.com/questions/57788508/static-variables-in-kotlin
    // https://stackoverflow.com/questions/24464663/why-is-my-counter-being-reset-each-time-after-call-tooncreate/24464738#24464738
    companion object {
        var count = 0
    }

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

        val hej = resources.getString(R.string.counter_text)
        val nmb = findViewById<TextView>(R.id.bathing_site_nmb)
        nmb.text = "$count $hej"
        invalidate()
        requestLayout()
    }
    
    fun increaseCount() {
        count += 1
        val nmb = findViewById<TextView>(R.id.bathing_site_nmb)
        val hej = resources.getString(R.string.counter_text)
        val textHej = "$count $hej"
        nmb.text = textHej
        invalidate()
        requestLayout()
    }
}
