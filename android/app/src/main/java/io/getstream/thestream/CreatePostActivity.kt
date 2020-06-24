package io.getstream.thestream

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.getstream.thestream.services.FeedService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

const val POST_SUCCESS = 99

class CreatePostActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val submit: Button = findViewById(R.id.btnSubmit)
        val postView: EditText = findViewById(R.id.post_text)

        submit.setOnClickListener {
            val text: String = postView.text.toString()

            launch(Dispatchers.IO) {
                FeedService.post(text)

                launch(Dispatchers.Main) {
                    setResult(POST_SUCCESS)
                    finish()
                }
            }
        }
    }
}