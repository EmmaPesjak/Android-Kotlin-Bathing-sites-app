package se.miun.empe2105.dt031g.bathingsites

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

/**
 * Custom info window class for the maps activity. Enables more information to be visible
 * in the snippet.
 * https://developers.google.com/codelabs/maps-platform/maps-platform-101-android#6
 * https://stackoverflow.com/questions/49675706/google-maps-snippet-show-more-text
 * https://www.youtube.com/watch?v=DhYofrJPzlI
 */
class CustomInfoWindowAdapter(context: Context) :
    GoogleMap.InfoWindowAdapter {
    @SuppressLint("InflateParams")
    private val mWindow: View =
        LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    /**
     * Creates the title and snippet.
     */
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

    /**
     * Provides a custom info window for a marker.
     */
    override fun getInfoWindow(marker: Marker): View {
        makeWindowText(marker, mWindow)
        return mWindow
    }

    /**
     * Provides a custom info window for a marker.
     */
    override fun getInfoContents(marker: Marker): View {
        makeWindowText(marker, mWindow)
        return mWindow
    }
}