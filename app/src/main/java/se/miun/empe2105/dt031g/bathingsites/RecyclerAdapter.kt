package se.miun.empe2105.dt031g.bathingsites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter class for the recycler view, provides a binding from a data set of saved
 * bathing sites.
 * https://www.youtube.com/watch?v=HtwDXRWjMcU
 * https://www.youtube.com/watch?v=Mc0XT58A1Z4
 */
class RecyclerAdapter(
    var bathingSites: List<BathingSite>
) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    // Allow for click listeners on the bathing sites. https://www.youtube.com/watch?v=WqrpcWXBz14
    var onItemClick : ((BathingSite) -> Unit)? = null

    /**
     * View holder class that holds the views of the recycler view.
     */
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * Inflate the view when created.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_row, parent, false)
        return RecyclerViewHolder(view)
    }

    /**
     * Displays the name of the site on a certain recycler position and sets a click listener
     * for the site.
     */
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.recycler_row_text).text = bathingSites[position].name

            findViewById<CardView>(R.id.recycler_card).setOnClickListener {
                onItemClick?.invoke(bathingSites[position])
            }
        }
    }

    /**
     * Returns the number of sites in the list.
     */
    override fun getItemCount(): Int {
        return bathingSites.size
    }
}
