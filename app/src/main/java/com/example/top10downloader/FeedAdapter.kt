package com.example.top10downloader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class ViewHolder(v: View) {
    val tvName: TextView = v.findViewById(R.id.tvName)
    val tvArtist: TextView = v.findViewById(R.id.tvArtist)
    val tvSummary: TextView = v.findViewById(R.id.tvSummary)
}

class FeedAdapter(
    context: Context,
    private val resource: Int,
    private var applications: List<FeedEntry>
) : ArrayAdapter<FeedEntry>(context, resource) {

   // private val TAG = "FeedAdapter"
    private val inflater = LayoutInflater.from(context)

    fun setFeedList(feedList: List<FeedEntry>){
        this.applications=feedList
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        // Log.d(TAG, "get count :called")
        return applications.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //  Log.d(TAG, "get view:called")
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            //  Log.d(TAG, "getView called with null convertView")
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }


        val currentapp = applications[position]

        viewHolder.tvName.text = currentapp.name
        viewHolder.tvArtist.text = currentapp.artist
        viewHolder.tvSummary.text = currentapp.summary

        return view
    }
}