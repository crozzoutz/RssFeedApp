package com.example.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class ParseApplications {
    private val TAG = "ParseApplication"
    val applications = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
        Log.d(TAG, "parse called with $xmlData")
        var status = true
        var inEntry = false
        var textvalue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name?.toLowerCase()  //TODO: we should use the safe call operator
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                      //  Log.d(TAG, "parse: starting tag for $tagName")
                        if (tagName == "entry") {
                            inEntry = true
                        }
                    }
                    XmlPullParser.TEXT -> textvalue = xpp.text

                    XmlPullParser.END_TAG -> {
                       // Log.d(TAG, "parse : Ending tag for $tagName")
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()  // creating new object
                                }

                                "name" -> currentRecord.name = textvalue
                                "artist" -> currentRecord.artist = textvalue
                                "releasedate" -> currentRecord.releaseDate = textvalue
                                "summary" -> currentRecord.summary = textvalue
                                "image" -> currentRecord.imageURL = textvalue
                            }
                        }
                    }
                }
                eventType = xpp.next()
            }

//            applications.forEach { app ->
//                Log.d(TAG, "*************************************")
//                Log.d(TAG, app.toString())
 //           }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            status = false
        }
        return status
    }

}