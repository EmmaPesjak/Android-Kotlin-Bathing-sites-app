package se.miun.empe2105.dt031g.bathingsites

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class BathingSitesView(
    context: Context, attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {


    var count = 0

    //instansvariabel fär antal sites?

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
    }

    // ingen aning om detta blir bra
    fun setNmb(number: Number) {
        val nmb = findViewById<TextView>(R.id.bathing_site_nmb)
        nmb.text = number.toString()
        invalidate()
        requestLayout()
    }

    companion object {

        var count = 0

        fun increaseSites(): Int {
            count += 1

            return count
        }
    }

    //kan man ha något save instance state här?
    //Eller ska allt detta vara i fragmentet???  nej instansvariablen ska vara här?
    // göra den till någon sorts util?



}