package se.miun.empe2105.dt031g.bathingsites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(
    var bathingSites: List<SavedBathingSite>
) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    //https://www.youtube.com/watch?v=WqrpcWXBz14
    var onItemClick : ((SavedBathingSite) -> Unit)? = null

    //viewholder class that holds the views of the recycler view
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        //sätta texten till namn och klicklistener
        holder.itemView.apply {

            findViewById<TextView>(R.id.recycler_row_text).text = bathingSites[position].name

            findViewById<CardView>(R.id.recycler_card).setOnClickListener {
                onItemClick?.invoke(bathingSites[position])
            }
        }
    }

    //antal sites
    override fun getItemCount(): Int {
        return bathingSites.size
    }
}