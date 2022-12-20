package se.miun.empe2105.dt031g.bathingsites

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*


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

    //private var weatherIsDownloaded = false

    companion object {
        var weatherIsDownloaded = false
    }

    // fast måste ha någon load först
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (weatherIsDownloaded) {
            return getWeatherDialog()
        } else {

            //executor istället??
//            runBlocking {
//                GlobalScope.launch {
//                    delay(5000)
//
//
//                }
//            }

            // glöm inte permissions

            weatherIsDownloaded = true



            return AlertDialog.Builder(activity)
                .setMessage(R.string.getting_current_weather)
                .create()

            //köra coroutinen direkt här inne istället?



        }

        //här ska nerladdningen statas? (men kolla om det redan fnns nerladdat väder så man kan gå in och ut ut rutan
        // utan att ladda ner igen) och så ska dialogen visa en progressbar,
        //sen ska detta visas: fast med väder

    }


    private lateinit var isdialog : AlertDialog
    fun hejDialog(activity: Activity) {

        val inflater = activity.layoutInflater

        val dialogView = inflater.inflate(R.layout.fragment_weather, null)
        val builder = AlertDialog.Builder(activity)

        builder.setView(dialogView)
        isdialog = builder.create()
        isdialog.show()
    }
    fun isDismiss() {
        isdialog.dismiss()
    }
    fun hejHopp(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
            .setTitle(R.string.current_weather)
            .setNegativeButton(R.string.ok
            ) { dialog, _ -> dialog.dismiss() }
            .create()
    }


    fun getWeatherDialog() : Dialog {
        return AlertDialog.Builder(activity)
            .setTitle(R.string.current_weather)
            .setNegativeButton(R.string.ok
            ) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    // använder ej
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment WeatherFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            WeatherFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}