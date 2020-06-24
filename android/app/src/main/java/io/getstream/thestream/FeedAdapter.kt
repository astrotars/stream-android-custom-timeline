package io.getstream.thestream

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.getstream.core.models.Activity


class FeedAdapter(
    val context: Context
) : RecyclerView.Adapter<FeedAdapter.VH>() {

    val objects = mutableListOf<Activity>()

    fun setData(activities: List<Activity>) {
        objects.clear()
        objects.addAll(activities)
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.feed_item, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(objects[position])
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(activity: Activity) {

            val author = activity.actor.replace("SU:", "")
            val message = activity.extra["message"] as String

            itemView.findViewById<TextView>(R.id.timeline_item_author_name).text = author
            itemView.findViewById<TextView>(R.id.timeline_item_message).text = message
        }
    }
}
