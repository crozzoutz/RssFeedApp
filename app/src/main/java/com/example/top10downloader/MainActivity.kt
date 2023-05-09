@file:Suppress("DEPRECATION")

package com.example.top10downloader

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.top10downloader.databinding.ActivityMainBinding

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""
}

private const val TAG = "MainActivity"
private const val STATE_URL = "feedUrl"
private const val STATE_LIMIT = "feedLimit"



class MainActivity : AppCompatActivity() {
    private val feedViewModel: FeedViewModel by lazy { ViewModelProvider(this)[FeedViewModel::class.java] }

    //by lazy { DownloadData(this, binding.xmlListView) }
    private lateinit var binding: ActivityMainBinding


    private var feedUrl: String =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Log.d(TAG, "onCreate: called")

        val feedAdapter = FeedAdapter(this, R.layout.list_record, EMPTY_FEED_LIST)
        binding.xmlListView.adapter = feedAdapter

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL)!!
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }


        feedViewModel.feedEntries.observe(this,
            Observer<List<FeedEntry>> { feedEntries -> feedAdapter.setFeedList(feedEntries) })



        feedViewModel.downLoadUrl(feedUrl.format(feedLimit))
        Log.d(TAG, "DownloadUrl: done")
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
            R.id.menuRefresh -> feedViewModel.invalidate()
            else ->
                return super.onOptionsItemSelected(item)
        }
        feedViewModel.downLoadUrl(feedUrl.format(feedLimit))
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedUrl)
        outState.putInt(STATE_LIMIT, feedLimit)
    }

}



