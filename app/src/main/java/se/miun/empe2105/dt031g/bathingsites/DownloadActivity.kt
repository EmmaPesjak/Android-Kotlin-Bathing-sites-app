package se.miun.empe2105.dt031g.bathingsites

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.*

/**
 * Download activity class. Downloads bathing sites from a webview and adds
 * them to the database.
 */
class DownloadActivity : AppCompatActivity() {

    private var downloadId: Long? = null
    private lateinit var manager: DownloadManager
    private lateinit var appDatabase: AppDatabase
    private lateinit var dbProgressDialog : ProgressDialog

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        appDatabase = AppDatabase.getDatabase(this)
        dbProgressDialog = ProgressDialog(this)

        // Set up the webView.
        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?
            ): Boolean { return false }
        }
        // Load the URL for the page. Get the url from settings.
        val preferences = getSharedPreferences("download", Context.MODE_PRIVATE)
        val url = preferences.getString("dlValue", "")
        if (url != null) {
            webView.loadUrl(url)
        }

        // Set download listener.
        webView.setDownloadListener { url, _, _, _, _ ->
            val downloadProgressDialog = ProgressDialog(this)
            val fileEnding: String =
                url.substringAfterLast("/")  // ex bathingsites1.csv
            // Download on a coroutine.
            runBlocking {
                withContext(Dispatchers.IO) {
                    GlobalScope.launch {

                        // Download with the help of a download manager.
                        val request = DownloadManager.Request(Uri.parse(url))
                        request.setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                        )
                        request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS.toString(),
                            fileEnding
                        )
                        manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        downloadId = manager.enqueue(request)

                        // Create a broadcast receiver that dismisses the progress dialog.
                        // https://stackoverflow.com/questions/21477493/android-download-manager-completed
                        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
                            override fun onReceive(ctxt: Context, intent: Intent) {
                                downloadProgressDialog.dismiss()
                            }
                        }
                        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                        }
                    }
                }

            // Show a progress dialog while downloading.
            val message = getString(R.string.downloading)
            downloadProgressDialog.setMessage(message)
            downloadProgressDialog.setCancelable(false)
            // Set a listener for dismiss so that the data will be added to the database when
            // the download is complete.
            downloadProgressDialog.setOnDismissListener {
                GlobalScope.launch {
                    addToDatabase(fileEnding)
                }
            }
            downloadProgressDialog.show()
        }
    }

    /**
     * Method for adding downloaded sites to the database.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun addToDatabase(fileEnding: String) {

        Dispatchers.Main {
            // Show a progress dialog while adding to the database.
            val message = getString(R.string.adding_to_db)
            dbProgressDialog.setMessage(message)
            dbProgressDialog.setCancelable(false)
            dbProgressDialog.show()

            GlobalScope.launch {
                // Read from input stream.
                val pathName = "" + Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS.toString()
                ) + "/" + fileEnding
                val downloadedFile = File(pathName)
                val fileInputStream =
                    withContext(Dispatchers.IO) {
                        FileInputStream(downloadedFile)
                    }
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                var text: String?
                while (run {
                        text = bufferedReader.readLine()
                        text
                    } != null) {

                    // Get values.
                    val coordinates = text?.substringBefore(",\"\"")
                        ?.substringAfterLast('"') //ex 15.6746,62.1556
                    val longitude = coordinates?.substringBefore(",") // ex 15.6746
                    val latitude = coordinates?.substringAfter(",")     // ex 62.1556
                    // Convert to floats.
                    val longitudeFloat = longitude?.toFloat()
                    val latitudeFloat = latitude?.toFloat()
                    // Check if the coordinates are unique.
                    val coordsExists: Boolean =
                        appDatabase.bathingSiteDao()
                            .coordsExists(longitudeFloat, latitudeFloat)

                    // Only add the site if the coordinates are unique.
                    if (!coordsExists) {

                        // Get the rest of the values.
                        val nameAndPossiblyAddress = text?.substringBefore("\"\"\"")
                            ?.substringAfterLast("\"\"")  // ex Viken, Västersjön, Ramsjö

                        val name = nameAndPossiblyAddress?.substringBefore(",") // ex Viken

                        // Check if there is an address, address exists if the string contains a comma.
                        val address: String = if (nameAndPossiblyAddress?.contains(",")
                            == true
                        ) {
                            nameAndPossiblyAddress.substringAfter(",")  // ex Västersjön, Ramsjö
                        } else {
                            ""
                        }

                        // Create the site.
                        val bathingSite = BathingSite(
                            null, name, null, address,
                            longitudeFloat, latitudeFloat,
                            null, null, null
                        )
                        // Add to the database.
                        appDatabase.bathingSiteDao().insert(bathingSite)
                        // Increase the count of bathing sites.
                        BathingSitesView.count += 1
                    }
                }
                // Close the input stream.
                withContext(Dispatchers.IO) {
                    fileInputStream.close()
                }
                // Set the download ID to null since the download is complete and it
                // does not need to be stored anymore.
                downloadId = null
                // Delete the downloaded file.
                downloadedFile.delete()
                // Dissmiss the dialog.
                dbProgressDialog.dismiss()
            }
        }}

    /**
     * Overridden method that cancels a current download if the user restarts the
     * application during download.
     */
    override fun onPause() {
        super.onPause()
        downloadId?.let { manager.remove(it) }
    }

    /**
     * Inflate the overflow menu.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    /**
     * Set option responses.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
