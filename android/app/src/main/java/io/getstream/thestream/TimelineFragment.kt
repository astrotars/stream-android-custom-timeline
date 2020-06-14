package io.getstream.thestream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import io.getstream.thestream.services.FeedService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class TimelineFragment : Fragment(), CoroutineScope by MainScope() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_timeline, container, false)
        val listView: ListView = rootView.findViewById<View>(R.id.list_timeline) as ListView
        val adapter = FeedAdapter(context!!, mutableListOf())

        listView.adapter = adapter

        launch(Dispatchers.IO) {
            val timelineFeed = FeedService.timelineFeed()
            launch(Dispatchers.Main) { adapter.addAll(timelineFeed) }
        }

        return rootView
    }
}
