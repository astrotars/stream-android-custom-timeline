package io.getstream.thestream

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.getstream.core.models.Activity
import kotlinx.android.synthetic.main.feed_item.view.*


class FeedAdapter(context: Context, objects: MutableList<Activity>) :
    ArrayAdapter<Activity>(context, android.R.layout.simple_list_item_1, objects) {

    private data class ViewHolder(
        val author: TextView,
        val message: TextView
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val streamActivity: Activity = getItem(position)!!
        val viewHolder: ViewHolder
        var newView = convertView

        if (newView == null) {
            val inflater = LayoutInflater.from(context)
            newView = inflater.inflate(R.layout.feed_item, parent, false)
            viewHolder = ViewHolder(
                newView.timeline_item_author_name as TextView,
                newView.timeline_item_message as TextView
            )
        } else {
            viewHolder = newView.tag as ViewHolder
        }

        viewHolder.author.text = streamActivity.actor
        viewHolder.message.text = streamActivity.extra["message"] as String

        newView!!.tag = viewHolder

        return newView
    }
}
