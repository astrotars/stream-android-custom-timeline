package io.getstream.thestream

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.getstream.thestream.services.BackendService
import io.getstream.thestream.services.FeedService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val submit: Button = findViewById(R.id.submit)
        val userView: EditText = findViewById(R.id.user)

        submit.setOnClickListener {
            val user: String = userView.text.toString()

            launch(Dispatchers.IO) {
                BackendService.signIn(user)

                val feedCredentials = BackendService.getFeedCredentials()

                launch(Dispatchers.Main) {
                    FeedService.init(user, feedCredentials)

                    startActivity(
                        Intent(applicationContext, AuthedMainActivity::class.java)
                    )
                }
            }
        }
    }
}
