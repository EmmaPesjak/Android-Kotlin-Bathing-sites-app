package se.miun.empe2105.dt031g.bathingsites

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * A simple [Fragment] subclass.
 */
class WeatherFragment : DialogFragment() {

    //Denna behövs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    //https://www.youtube.com/watch?v=GxET0CYy1eg

    //https://stackoverflow.com/questions/18601049/adding-positive-negative-button-to-dialogfragments-dialog

    private lateinit var alertDialog : AlertDialog
    private lateinit var progressDialog: ProgressDialog


    //ladda alltid ner nytt väder eftersom det kan ändras.
    fun createDialog(activity: Activity, address: String? = "", longitude: Int? = null, latitude: Int? = null) {


        //https://www.baeldung.com/kotlin/create-thread-pool
        val service: ExecutorService = Executors.newSingleThreadExecutor()

        service.submit {


            //denna ska ju hämtas från settings och lägga till lat/long/adress
            val url = "https://dt031g.programvaruteknik.nu/bathingsites/weather.php"


            if (longitude != null && latitude != null) {
                println(latitude)
                println(longitude)
                //ladda ner med coordinater


                //borde ha någon timeout här eller validering av mög
                //behöver fixa ÅÄÖ

                val completeUrl = "$url?lat=$latitude&lon=$longitude"
                println(completeUrl)
                getWeatherData(completeUrl)


            } else if (address != "") {
                println(address)
                // ladda ner med adress


                val completeUrl = "$url?q=$address"
                println(completeUrl)
                getWeatherData(completeUrl)


                // måste fixa något ifall staden/koordinaterna inte fungerar
            } else {
                println("Should never occur")
                // måste få antingen adress eller coordinater så borde inte behöve göra mer här?
            }

            //alertDialog.dismiss()
            progressDialog.dismiss()
        }

        val message = activity.getString(R.string.getting_current_weather)

        progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.setOnDismissListener {
            showWeatherDialog(activity)
        }
        progressDialog.show()


        //inflatea fragmentet med progressbaren
//        val inflater = activity.layoutInflater
//        val dialogView = inflater.inflate(R.layout.fragment_weather, null)
//        val builder = AlertDialog.Builder(activity)
//        builder.setView(dialogView)
//        alertDialog = builder.create()
//
//        //https://medium.com/@stlin813/sequentially-show-multiple-dialogs-in-rxjava-5b57b2b9595b
//        //sätt en listener så vädret kan visas sen
//        alertDialog.setOnDismissListener(DialogInterface.OnDismissListener {
//            showWeatherDialog(activity)
//        })
//
//        //visa dialogen
//        alertDialog.show()

    }


    //https://stackoverflow.com/questions/44883593/how-to-read-json-from-url-using-kotlin-android
    fun parseResponse(json: String): JSONObject? {
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(json)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    //använder inte inputstream här som i tipset
    fun getWeatherData(url: String) {
        val apiResponse = URL(url).readText()

        val jsonObject = parseResponse(apiResponse)

        //https://johncodeos.com/how-to-parse-json-in-android-using-kotlin/
        val weatherJsonArray = jsonObject?.getJSONArray("weather")
        val icon = weatherJsonArray?.getJSONObject(0)?.getString("icon")
        weatherDescription = weatherJsonArray?.getJSONObject(0)?.getString("description").toString()
        val mainJsonObj = jsonObject?.getJSONObject("main")
        temperature = mainJsonObj?.get("temp").toString()


        // IN MED DETTA I STRINGS ELLER NÅGOT
        val imgUrl = "https://openweathermap.org/img/w/$icon.png"
        val stream = URL(imgUrl).openStream()
        val drawable = Drawable.createFromStream(stream, "src")
        weatherIcon = drawable
    }

    lateinit var temperature: String  //avrunda?
    lateinit var weatherDescription: String
    private lateinit var weatherIcon : Drawable

    private fun showWeatherDialog(activity: Activity) {

        //https://stackoverflow.com/questions/18601049/adding-positive-negative-button-to-dialogfragments-dialog
        //https://stackoverflow.com/questions/36699987/show-degree-symbol-in-a-textview


        //gör om xmlen och inflatea så det ser ordentligt ut

        AlertDialog.Builder(activity)
            .setTitle(R.string.current_weather)
            .setMessage("$weatherDescription \n$temperature\u2103") //hårdkoda inte? in i strings?
            .setIcon(weatherIcon)
            .setNegativeButton(R.string.ok
            ) { dialog, _ -> dialog.dismiss() }
            .create().show()
    }
}