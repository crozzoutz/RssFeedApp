@file:Suppress("DEPRECATION")

package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.top10downloader.databinding.ActivityMainBinding
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""


}

class MainActivity : AppCompatActivity() {

    private var downloadData: DownloadData? = null

    //by lazy { DownloadData(this, binding.xmlListView) }
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    private var feedUrl: String =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10

    private var feedCachedUrl = "INVALIDATED"
    private var STATE_URL = "feedUrl"
    private var STATE_LIMIT = "feedLimit"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Log.d(TAG, "onCreate: called")

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL)!!
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }
        //val downloadData = DownloadData(this,binding.xmlListView)
        downLoadUrl(feedUrl.format(feedLimit))
        Log.d(TAG, "DownloadUrl: done")
    }

    private fun downLoadUrl(feedUrl: String) {
        if (feedUrl != feedCachedUrl) {
            Log.d(TAG, "downloadUrl: starting AsyncTask")
            downloadData = DownloadData(this, binding.xmlListView)
            downloadData?.execute(feedUrl)
            feedCachedUrl = feedUrl
            Log.d(TAG, "downloadUrl: done")
        } else
            Log.d(TAG, "downloadUrl - URL not changed")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if (feedLimit == 10) {
            menu?.findItem(R.id.menu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.menu25)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuFree ->
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.menuPaid ->
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.menuSongs ->
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.menu10, R.id.menu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(TAG, "onOption: ${item.title} setting feedLimit to $feedLimit")
                } else {
                    Log.d(TAG, "onOption: ${item.title} setting feedLimit unchanged")

                }
            }
            R.id.menuRefresh -> feedCachedUrl = "INVALIDATED"
            else ->
                return super.onOptionsItemSelected(item)
        }
        downLoadUrl(feedUrl.format(feedLimit))
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedUrl)
        outState.putInt(STATE_LIMIT, feedLimit)
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    companion object {
        private class DownloadData(context: Context, listView: ListView) :
            AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"

            var propertyContext: Context by Delegates.notNull()
            var propertyListView: ListView by Delegates.notNull()

            init {
                propertyContext = context
                propertyListView = listView
            }

            @Deprecated("Deprecated in Java")
            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                //Log.d(TAG, "onPostexecute : parameter is $result")

                //now create a parser
                val parseApplications = ParseApplications()
                parseApplications.parse(result)


                val feedAdapter = FeedAdapter(
                    propertyContext,
                    R.layout.list_record,
                    parseApplications.applications
                )
                propertyListView.adapter = feedAdapter
            }

            @Deprecated("Deprecated in Java")
            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "error downloading in background")
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }

    }

}


