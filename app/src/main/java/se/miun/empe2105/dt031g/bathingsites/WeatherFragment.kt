package se.miun.empe2105.dt031g.bathingsites

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Fragment for the weather dialog. Yes, the code is messy :)
 */
class WeatherFragment : DialogFragment() {

    private lateinit var weatherIcon : Drawable
    private lateinit var temperature: String
    private lateinit var weatherDescription: String
    private lateinit var imageUrl: String
    private lateinit var couldNotFind: String
    private lateinit var badUrl: String
    private lateinit var celsius: String
    private lateinit var png: String

    /**
     * Function for searching for the weather using coordinates or address.
     * Displays a progress dialog while fetching and a weather/could not find/error
     * dialog when finished fetching. Since weather changes over time, values are not
     * stored and the function always fetches the weather even if the user searches
     * the same place twice or more times.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun searchWeather(activity: Activity, address: String? = "", longitude: Int? = null, latitude: Int? = null) {

        val progressDialog = ProgressDialog(activity)

        // Get strings from the activity, these are used later.
        couldNotFind = activity.resources.getString(R.string.could_not_find)
        badUrl = activity.resources.getString(R.string.bad_url)
        imageUrl = activity.resources.getString(R.string.image_url)
        celsius = activity.resources.getString(R.string.celsius)
        png = activity.resources.getString(R.string.png)

        // Fetch the data with an executor service. https://www.baeldung.com/kotlin/create-thread-pool
        val service: ExecutorService = Executors.newSingleThreadExecutor()
        service.submit {
            // Get the url from settings.
            val preferences = activity.getSharedPreferences("fetch", Context.MODE_PRIVATE)
            val url = preferences.getString("value", "")

            // In first hand, download with coordinates.
            if (longitude != null && latitude != null) {
                val completeUrl = "$url?lat=$latitude&lon=$longitude"

                // Launch with timeout or null, catch possible errors.
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        withTimeoutOrNull(5000) {
                            try {
                                getWeatherData(completeUrl)
                                // Dismiss the progress dialog, this also triggers displaying the weather dialog.
                                progressDialog.dismiss()
                            } catch (e: Exception) {
                                when(e) {
                                    is MalformedURLException,
                                        is FileNotFoundException -> {
                                        weatherDescription = badUrl
                                        // Dismiss the progress dialog, this also triggers displaying the error.
                                        progressDialog.dismiss()
                                    }
                                    else -> throw e
                                }
                            }
                        }
                    }
                }
            } else if (address != "") {

                val town = address?.substringAfterLast(" ")

                // Otherwise download with the address.
                val completeUrl = "$url?q=$town"

                // Launch with timeout or null, catch possible errors.
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        withTimeoutOrNull(5000) {
                            try {
                                getWeatherData(completeUrl)
                                // Dismiss the progress dialog, this also triggers displaying the weather dialog.
                                progressDialog.dismiss()
                            } catch (e: Exception) {
                                when (e) {
                                    is MalformedURLException,
                                    is FileNotFoundException -> {
                                        weatherDescription = badUrl
                                        // Dismiss the progress dialog, this also triggers displaying the error.
                                        progressDialog.dismiss()
                                    }
                                    else -> throw e
                                }
                            }
                        }
                    }
                }
            }
        }

        // Show a progress dialog while fetching weather data.
        val message = activity.getString(R.string.getting_current_weather)
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        // Set a listener for dismiss so that a weather dialog is shown.
        progressDialog.setOnDismissListener {
            showWeatherDialog(activity)
        }
        progressDialog.show()
    }

    /**
     * Function for parsing to JSON. Returns a [JSONObject].
     * https://stackoverflow.com/questions/44883593/how-to-read-json-from-url-using-kotlin-android
     */
    private fun parseResponse(jsonString: String): JSONObject? {
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(jsonString)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    /**
     * Function for fetching the weather data and storing it in the variables.
     */
    private fun getWeatherData(url: String) {

        // Read data and put in a string.
        val apiResponse = URL(url).readText()
        // Parse to JSON
        val jsonObject = parseResponse(apiResponse)
        // Get the status code.
        val code = jsonObject?.getString("cod")
        // Get the rest of the data if the code was 200.
        if (code == "200") {
            //https://johncodeos.com/how-to-parse-json-in-android-using-kotlin/

            // Get the description.
            val weatherJsonArray = jsonObject.getJSONArray("weather")
            weatherDescription = weatherJsonArray.getJSONObject(0)?.getString("description").toString()

            // Get the temperature and round it down.
            val mainJsonObj = jsonObject.getJSONObject("main")
            val roundedTemp = mainJsonObj.get("temp").toString().toDouble().toInt()
            temperature = roundedTemp.toString()

            // Get the drawable weather icon.
            val icon = weatherJsonArray.getJSONObject(0)?.getString("icon")
            val finishedImgUrl = imageUrl + icon + png
            val stream = URL(finishedImgUrl).openStream()
            val drawable = Drawable.createFromStream(stream, "src")
            weatherIcon = drawable
        } else { // Else set the description to could not find.
            weatherDescription = couldNotFind
        }
    }


    /**
     * Function for showing the weather dialog in the activity.
     * https://stackoverflow.com/questions/18601049/adding-positive-negative-button-to-dialogfragments-dialog
     * https://stackoverflow.com/questions/36699987/show-degree-symbol-in-a-textview
     */
    private fun showWeatherDialog(activity: Activity) {
        // Check if weather data was found.
        if (weatherDescription != couldNotFind && weatherDescription != badUrl) {

            // Inflate the fragment.
            val alertDialog : AlertDialog
            val inflater = activity.layoutInflater
            val dialogView = inflater.inflate(R.layout.fragment_weather, null)
            val builder = AlertDialog.Builder(activity)
            builder.setView(dialogView)

            val desc = dialogView.findViewById<TextView>(R.id.weather_description)
            val temp = dialogView.findViewById<TextView>(R.id.weather_temp)
            val icon = dialogView.findViewById<ImageView>(R.id.weather_icon)

            // Set title, button and data.
            builder.setTitle(R.string.current_weather)
            builder.setNegativeButton(R.string.ok
            ) { dialog, _ -> dialog.dismiss() }

            desc.text = weatherDescription
            val tempText = temperature + celsius
            temp.text = tempText
            icon.setImageDrawable(weatherIcon)

            alertDialog = builder.create()
            alertDialog.show()
        } else { // If it was not found, display a dialog telling it.
            AlertDialog.Builder(activity)
                .setTitle(weatherDescription)
                .setNegativeButton(R.string.ok
                ) { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}
