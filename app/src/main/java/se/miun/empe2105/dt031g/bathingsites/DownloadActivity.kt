package se.miun.empe2105.dt031g.bathingsites

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.*

class DownloadActivity : AppCompatActivity() {


    private var downloadId: Long? = null
    private lateinit var manager: DownloadManager
    private lateinit var appDatabase: AppDatabase

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        appDatabase = AppDatabase.getDatabase(this)

        //set up the webView.
        val webView = findViewById<WebView>(R.id.webView)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?
            ): Boolean { return false }
        }

        // Load the URL for the page.
        // Get the url from settings.
        val preferences = getSharedPreferences("download", Context.MODE_PRIVATE)
        val url = preferences.getString("dlValue", "")
        if (url != null) {
            webView.loadUrl(url)
        }




        webView.setDownloadListener { url, _, _, _, _ ->
            // Download on a coroutine.
            runBlocking {
                GlobalScope.launch {


                    val fileEnding: String = url.substringAfterLast("/")  // ex bathingsites1.csv
                    val dirName: String = fileEnding.substringBefore(".")  // ex bathingsites1

                    // Download the file if it is not already downloaded.
                    if(!checkIfDownloaded()) {
                        // Download with the help of a download manager.
                        val request = DownloadManager.Request(Uri.parse(url))
                        request.setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                        )
                        request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            dirName
                        )
                        manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        downloadId = manager.enqueue(request)


                        // Calculate the progress for the progressbar during download.
                        var downloading = true
                        while (downloading) {
                            val q = DownloadManager.Query()
                            q.setFilterById(downloadId!!)
                            val cursor: Cursor = manager.query(q)
                            cursor.moveToFirst()
                            val bytesDownloaded: Int = cursor.getInt(
                                cursor
                                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                            )
                            val bytesTotal: Int = cursor.getInt(
                                cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                            )
                            if (cursor.getInt(
                                    cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                                ) == DownloadManager.STATUS_SUCCESSFUL
                            ) {
                                downloading = false
                            }
                            val dlProgress: Int = ((bytesDownloaded * 100) / bytesTotal)
                            runOnUiThread {
                                showProgress(dlProgress)
                                showProgressText(dirName)
                            }
                            cursor.close()
                            delay(50) // Tiny delay so the loop doesn't go crazy.
                        }

                        setProgressbarInvisible()




                        // sen här ska alla badplatser sparas i databasen och
                        // sen ska det raderas från enheten
                        val pathName = "" + Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                        ) + "/" + dirName
                        val downloadedFile = File(pathName)

                        var fileInputStream = FileInputStream(downloadedFile)
                        var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
                        val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)

                        var text: String? = null
                        while (run {
                                text = bufferedReader.readLine()
                                text
                            } != null) {



                            println(text)
                            // här måste det delas och sparas

//                            val tjena = text?.split(",")?.toTypedArray()  //här kan det splittas för mycket :(
//
//                            println("longi: " + tjena?.get(0)?.substringAfterLast('"'))
//                            println("lati: " + tjena?.get(1))
//                            println(println("name: " + tjena?.get(2)?.substringAfterLast('"')))  //detta blir fel om det inte finns en adress :(
//
//                            val hej = tjena?.drop(3)
//                            if (hej != null) {
//                                hej.forEach {
//                                    println(it.substringBefore('"').substringAfterLast(" "))
//                                }
//
//
////                            if (hej != null) {
////                                println("address: " + (tjena?.drop(3)?.substringBefore('"')))
////                            }
////                            var longitude = text?.substringBefore(",")
////                            println(longitude)
////
////                            var latitude = text?.substringBefore(",")
////                            println(latitude)
//                            }
                            //println(" ")

                            val coords = text?.substringBefore(",\"\"")?.substringAfterLast('"')
                            val longi = coords?.substringBefore(",")
                            val lati = coords?.substringAfter(",")

                            val nameAndPossiblyAddress = text?.substringBefore("\"\"\"")
                                ?.substringAfterLast("\"\"")

                            println("Coords?: $coords")
                            println("longi: $longi")
                            println("lati: $lati")
                            println("Name + address?: $nameAndPossiblyAddress")

                            println(" ")


                        }
                        fileInputStream.close()



                        // Set the download ID to null since the download is complete and it
                        // does not need to be stored anymore.
                        downloadId = null
                    }
                }
            }
        }

    }

    // Update the progressbar with the current progress.
    private fun showProgress(vararg progress: Int) {
        val progressBar = findViewById<ProgressBar>(R.id.progressbar)
        progressBar.visibility = View.VISIBLE
        progressBar.progress = progress[0]
    }

    // Make the progressbar and text invisible.
    private fun setProgressbarInvisible()  {
        val progressBar = findViewById<ProgressBar>(R.id.progressbar)
        progressBar.visibility = View.INVISIBLE
        val progressBarText = findViewById<TextView>(R.id.progressbarText)
        progressBarText.visibility = View.INVISIBLE
        println("hallå?")
    }

    // Make the text on the progressbar visible.
    private fun showProgressText(text: String) {
        val progressBarText = findViewById<TextView>(R.id.progressbarText)
        progressBarText.visibility = View.VISIBLE
        val progressText = " ${getString(R.string.downloading)} $text"
        progressBarText.text = progressText
    }


    private fun checkIfDownloaded(): Boolean {
        return false
    }

    //Behövs detta???? JA?
    // Overridden method that cancels a current download if the user restarts the
    // application during download.
    override fun onPause() {
        super.onPause()
        downloadId?.let { manager.remove(it) }
    }
}