package io.getstream.thestream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import io.getstream.thestream.services.FeedService
import kotlinx.android.synthetic.main.fragment_timeline.*
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
        val adapter = FeedAdapter(context!!)
        list_timeline.adapter = adapter

        try {
            launch(Dispatchers.IO) {
                val timelineFeed = FeedService.timelineFeed()
                launch(Dispatchers.Main) {
                    adapter.setData(timelineFeed)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            launch(Dispatchers.Main) {
                showError(e.message)
            }

        }

        return rootView
    }

    private fun showError(message: String?) {

        AlertDialog.Builder(context!!)
            .setTitle(R.string.error)
            .setMessage(message ?: getString(R.string.unknown_error))
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
