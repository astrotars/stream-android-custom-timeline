package io.getstream.thestream

import android.content.Intent
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

class ProfileFragment : Fragment(), CoroutineScope by MainScope() {
    private lateinit var adapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val listView: ListView = rootView.findViewById(R.id.list_profile_feed)

        adapter = FeedAdapter(rootView.context, mutableListOf())
        listView.adapter = adapter

        val newPost: View = rootView.findViewById(R.id.new_post)
        newPost.setOnClickListener {
            startActivityForResult(
                Intent(rootView.context, CreatePostActivity::class.java),
                POST_SUCCESS
            )
        }

        loadProfileFeed()

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == POST_SUCCESS) {
            loadProfileFeed()
        }
    }

    private fun loadProfileFeed() {
        launch(Dispatchers.IO) {
            val profileFeed = FeedService.profileFeed()

            launch(Dispatchers.Main) {
                adapter.clear()
                adapter.addAll(profileFeed)
            }
        }
    }
}
