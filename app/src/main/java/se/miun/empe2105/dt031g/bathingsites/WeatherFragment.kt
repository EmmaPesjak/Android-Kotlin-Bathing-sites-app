package se.miun.empe2105.dt031g.bathingsites

import android.app.Activity
import android.app.AlertDialog
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
import java.io.InputStream
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //använder ej?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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


//    companion object {
//        var weatherIsDownloaded = false
//    }
//
//    // fast måste ha någon load först
//    @OptIn(DelicateCoroutinesApi::class)
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//
//        if (weatherIsDownloaded) {
//            return getWeatherDialog()
//        } else {
//
//            //executor istället??
////            runBlocking {
////                GlobalScope.launch {
////                    delay(5000)
////                }
////            }
//
//            //https://www.baeldung.com/kotlin/create-thread-pool
//            val service: ExecutorService = Executors.newSingleThreadExecutor();
//            println("I am working hard")
//            service.submit {
//                Thread.sleep(5000) // Imitate slow IO
//                println("I am reporting on the progress")
//
//                //ladda ner möget
//
//                isDismiss()
//                println("would like to show new dialog now")
//
//                //problemet här är väl att dialogen inte visas i rätt activity...
//                // sätta allt utanför detta?
//                getWeatherDialog().show()
//            }
//            println("Meanwhile I continue to work")
//
////            service.execute(Runnable {
////                override fun run() {
////
////                }
////            })
//
//            //fixa om man stänger ner appen??
//
//            // glöm inte permissions
//
//
//            //får kanske kolla på riktigt om den fins i internal storage
//            //för nu laddar den ner varje gång man startar om
//            weatherIsDownloaded = true
//
//
//            val inflater = activity?.layoutInflater
//
//            val dialogView = inflater?.inflate(R.layout.fragment_weather, null)
//            val builder = AlertDialog.Builder(activity)
//
//            builder.setView(dialogView)
//            isdialog = builder.create()
//
//            return isdialog
//
////            return AlertDialog.Builder(activity)
////                .setMessage(R.string.getting_current_weather)
////                .create()
//
//        }
//
//        //här ska nerladdningen statas? (men kolla om det redan fnns nerladdat väder så man kan gå in och ut ut rutan
//        // utan att ladda ner igen) och så ska dialogen visa en progressbar,
//        //sen ska detta visas: fast med väder
//
//    }


    private lateinit var alertDialog : AlertDialog
    //private lateinit var activityAdd : Activity


    //ladda alltid ner nytt väder eftersom det kan ändras.
    fun createDialog(activity: Activity, address: String? = "", longitude: Int? = null, latitude: Int? = null) {

        //activityAdd = activity  //detta kan raderas? använder bara den här


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

            dismissDialog()

            //får kanske kolla på riktigt om den fins i internal storage
            //för nu laddar den ner varje gång man startar om

        }


        //fixa om man stänger ner appen??

        // ska den hämtade datan sparas? kan den laddas ner på nytt varje gång? ska nerladdningen raderas?


        //inflatea fragmentet med progressbaren
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_weather, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        alertDialog = builder.create()

        //https://medium.com/@stlin813/sequentially-show-multiple-dialogs-in-rxjava-5b57b2b9595b
        //sätt en listener så vädret kan visas sen
        alertDialog.setOnDismissListener(DialogInterface.OnDismissListener {
            showWeatherDialog(activity)
        })

        //visa dialogen
        alertDialog.show()

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

        val weatherJsonArray = jsonObject?.getJSONArray("weather")
        val icon = weatherJsonArray?.getJSONObject(0)?.getString("icon")
        println("Icon:  $icon")
        val description = weatherJsonArray?.getJSONObject(0)?.getString("description")
        println("Description:  $description")

        weatherDescription = description.toString()


        val mainJsonObj = jsonObject?.getJSONObject("main")
        val temp = mainJsonObj?.get("temp")
        println("Temp: $temp")
        temperature = temp.toString()

        val coordJsonObj = jsonObject?.getJSONObject("coord")
        val lat = coordJsonObj?.get("lat")
        println("Lat: $lat")
        val longi = coordJsonObj?.get("lon")
        println("Long: $longi")

        val source = "https://openweathermap.org/img/w/$icon.png"

        val inputStream: InputStream = requireContext().assets.open(source)
        val d = Drawable.createFromStream(inputStream, null)
        d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
        weatherIcon = d
    }

    lateinit var temperature: String  //avrunda?
    lateinit var weatherDescription: String
    lateinit var weatherIcon: Drawable

    private fun dismissDialog() {
        alertDialog.dismiss()
    }

    private fun showWeatherDialog(activity: Activity) {

        //https://stackoverflow.com/questions/18601049/adding-positive-negative-button-to-dialogfragments-dialog

        val builder = AlertDialog.Builder(activity)
            .setTitle(R.string.current_weather)
            .setMessage("$weatherIcon $weatherDescription \n$temperature\u2103") //hårdkoda inte? in i strings?
            .setNegativeButton(R.string.ok
            ) { dialog, _ -> dialog.dismiss() }
            .create().show()

    }
}