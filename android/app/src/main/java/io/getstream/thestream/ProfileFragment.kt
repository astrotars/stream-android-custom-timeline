package io.getstream.thestream

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import io.getstream.thestream.services.FeedService
import kotlinx.android.synthetic.main.fragment_profile.*
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

        adapter = FeedAdapter(rootView.context)
        recyclerProfileFeed.adapter = adapter

        btnNewPost.setOnClickListener {
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
        try {
            launch(Dispatchers.IO) {
                val profileFeed = FeedService.profileFeed()

                launch(Dispatchers.Main) {
                    adapter.setData(profileFeed)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showError(e.message)
        }
    }

    private fun showError(message: String?) {

        AlertDialog.Builder(context!!)
            .setTitle(R.string.error)
            .setMessage(message ?: getString(R.string.unknown_error))
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
