package io.getstream.thestream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import io.getstream.thestream.services.BackendService
import io.getstream.thestream.services.FeedService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class PeopleFragment : Fragment(), CoroutineScope by MainScope() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_people, container, false)
        val list: ListView = rootView.findViewById(R.id.list_people)

        val adapter = ArrayAdapter(
            rootView.context,
            android.R.layout.simple_list_item_1,
            mutableListOf<String>()
        )
        list.adapter = adapter

        list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(rootView.context)

            alertDialogBuilder.setTitle("Pick an action")
            alertDialogBuilder.setPositiveButton("Follow") { dialog, _ ->
                val otherUser = adapter.getItem(position).toString()
                FeedService.follow(otherUser)

                dialog.dismiss()

                Toast
                    .makeText(context, "Successfully followed $otherUser", Toast.LENGTH_LONG)
                    .show()
            }

            alertDialogBuilder.setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }

            alertDialogBuilder.show()
        }

        launch(Dispatchers.IO) {
            val users = BackendService.getUsers()

            launch(Dispatchers.Main) { adapter.addAll(users) }
        }

        return rootView
    }

}
