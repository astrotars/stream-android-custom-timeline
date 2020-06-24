package io.getstream.thestream

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.getstream.thestream.services.BackendService
import io.getstream.thestream.services.FeedService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSubmit.setOnClickListener {

            val user = editUser.text.toString()

            launch(Dispatchers.IO) {

                try {
                    BackendService.signIn(user)

                    val feedCredentials = BackendService.getFeedCredentials()

                    launch(Dispatchers.Main) {
                        FeedService.init(user, feedCredentials)

                        startActivity(
                            Intent(applicationContext, AuthedMainActivity::class.java)
                        )
                    }

                } catch (t: Throwable) {
                    t.printStackTrace()
                    launch(Dispatchers.Main) {
                        showError(t.message)
                    }
                }
            }
        }
    }

    private fun showError(message: String?) {
        AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(message ?: getString(R.string.unknown_error))
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
