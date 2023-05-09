package com.example.top10downloader

import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

private const val TAG = "DownloadData"

 class DownloadData(private val callBack: DownloaderCallBack) :
    AsyncTask<String, Void, String>() {

    interface DownloaderCallBack {
        fun onDataAvailable(data: List<FeedEntry>)
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: String) {


        //now create a parser
        val parseApplications = ParseApplications()
        if (result.isNotEmpty()) {
            parseApplications.parse(result)
        }
        callBack.onDataAvailable(parseApplications.applications)

    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg url: String): String {
        Log.d(TAG, "doInBackground starts with ${url[0]}")
        val rssFeed = downloadXML(url[0])
        if (rssFeed.isEmpty()) {
            Log.e(TAG, "error downloading in background")
        }
        return rssFeed
    }

    private fun downloadXML(urlPath: String): String {
        try {
            return URL(urlPath).readText()
        }catch (e:MalformedURLException){
            Log.d(TAG,"Invalid URL"+ e.message)
        }catch (e:IOException){
            Log.d(TAG,"IO exception reading data "+ e.message)
        }catch (e:SecurityException){
            Log.d(TAG,"Security Exception needs Permission "+  e.message)
            e.printStackTrace()
        }
        return ""
    }
}