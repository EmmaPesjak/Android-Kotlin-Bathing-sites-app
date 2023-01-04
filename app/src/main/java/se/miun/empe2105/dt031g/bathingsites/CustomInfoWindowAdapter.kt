package se.miun.empe2105.dt031g.bathingsites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

//https://stackoverflow.com/questions/49675706/google-maps-snippet-show-more-text
//https://www.youtube.com/watch?v=DhYofrJPzlI
class CustomInfoWindowAdapter(context: Context) :
    GoogleMap.InfoWindowAdapter {
    private val mWindow: View =
        LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    private fun makeWindowText(marker: Marker, view: View) {
        val title = marker.title
        val textViewTitle = view.findViewById<View>(R.id.maps_title) as TextView
        if (title != "") {
            textViewTitle.text = title
        }
        val snippet = marker.snippet
        val textViewSnippet = view.findViewById<View>(R.id.maps_snippet) as TextView
        if (snippet != "") {
            textViewSnippet.text = snippet
        }
    }

    override fun getInfoWindow(marker: Marker): View {
        makeWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View {
        makeWindowText(marker, mWindow)
        return mWindow
    }
}