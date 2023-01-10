package se.miun.empe2105.dt031g.bathingsites

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.*

/**
 * Download activity class. Downloads bathing sites from a php-site and adds
 * them to the database.
 */
class DownloadActivity : AppCompatActivity() {

    private var downloadId: Long? = null
    private lateinit var manager: DownloadManager
    private lateinit var appDatabase: AppDatabase

    @OptIn(DelicateCoroutinesApi::class)
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
        // Load the URL for the page. Get the url from settings.
        val preferences = getSharedPreferences("download", Context.MODE_PRIVATE)
        val url = preferences.getString("dlValue", "")
        if (url != null) {
            webView.loadUrl(url)
        }

        // Set download listener.
        webView.setDownloadListener { url, _, _, _, _ ->
            // Download on a coroutine.
            runBlocking {
                withContext(Dispatchers.IO) {
                    GlobalScope.launch {



                        // Download with the help of a download manager.
                        val fileEnding: String =
                            url.substringAfterLast("/")  // ex bathingsites1.csv
                        val dirName: String = fileEnding.substringBefore(".")  // ex bathingsites1

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
                            delay(100) // Tiny delay so the loop doesn't go crazy.
                        }


                        try{
                            // Download complete. Now change the text of the progressbar.
                            val progressBarText = findViewById<TextView>(R.id.progressbarText)
                            progressBarText.text = getString(R.string.adding_to_db)

                            // Read from input stream.
                            val pathName = "" + Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS
                            ) + "/" + dirName
                            val downloadedFile = File(pathName)
                            val fileInputStream = FileInputStream(downloadedFile)
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
                            // Delete the downloaded file.
                            downloadedFile.delete()
                            // Set the progressbar invisible.
                            setProgressbarInvisible()
                            // Set the download ID to null since the download is complete and it
                            // does not need to be stored anymore.
                            downloadId = null
                        } catch (e: FileNotFoundException) {
                            Dispatchers.Main {
                                AlertDialog.Builder(this@DownloadActivity)
                                    .setMessage("Something went wrong, try again")
                                    .setNegativeButton(R.string.ok
                                    ) { dialog, _ -> dialog.dismiss() }
                                    .show()
                            }
                            println(e)
                            val pathName = "" + Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS
                            ) + "/" + dirName
                            val downloadedFile = File(pathName)
                            downloadedFile.delete()
                            setProgressbarInvisible()

                        }
                    }
                }
            }
        }
    }

    /**
     * Update the progressbar with the current progress.
     */
    private fun showProgress(vararg progress: Int) {
        val progressBar = findViewById<ProgressBar>(R.id.progressbar)
        progressBar.visibility = View.VISIBLE
        progressBar.progress = progress[0]
    }

    /**
     * Make the progressbar and text invisible.
     */
    private fun setProgressbarInvisible()  {
        val progressBar = findViewById<ProgressBar>(R.id.progressbar)
        progressBar.visibility = View.INVISIBLE
        val progressBarText = findViewById<TextView>(R.id.progressbarText)
        progressBarText.visibility = View.INVISIBLE
    }

    /**
     * Make the text on the progressbar visible.
     */
    private fun showProgressText(text: String) {
        val progressBarText = findViewById<TextView>(R.id.progressbarText)
        progressBarText.visibility = View.VISIBLE
        val progressText = " ${getString(R.string.downloading)} $text"
        progressBarText.text = progressText
    }

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
