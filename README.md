# Building a Custom Timeline with Stream Activity Feeds and Kotlin on Android

In this post, we'll be creating a simple social network, called The Stream, that allows a user to post messages to followers.

The app will allow a user to post a message to their followers. [Stream's Activity Feed API](https://getstream.io/activity-feeds/) combined with Android makes it straightforward to build this sort of complex interaction. All source code for this application is available on [GitHub](https://github.com/psylinse/stream-android-custom-timeline). This application is fully functional on Android.

Often there is context around those code snippets which are important, such as layout or navigation. Please refer to the full source if you're confused on how something works, what libraries and version are used, or how we got to a screen. Each snippet will be accompanied by a comment explaining which file and line it came from.

## Building The Stream

To build our social network we'll need both a backend and a mobile application. Most of the work is done in the mobile application, but we need the backend to securely create frontend tokens for interacting with the Stream API.

For the backend, we'll rely on [Express](https://expressjs.com/) ([Node.js](https://nodejs.org/en/)) leveraging [Stream Feed's JavaScript](https://github.com/GetStream/stream-js) library.

For the frontend, we'll build it with Kotlin wrapping [Stream Feed's Java](https://github.com/GetStream/stream-java) library. To post a message, the mobile application goes throught his flow:

* User types their name into our mobile application to log in.
* The Android app registers user with our backend and receives a Stream Activity Feed [frontend token](https://getstream.io/blog/integrating-with-stream-backend-frontend-options/).
* User types in their message and hits "Post". The mobile app uses the Stream token to create a Stream activity via Stream's REST API via the [Java library](https://github.com/GetStream/stream-java).
* User views their posts. The mobile app does this by retrieving its user feed via Stream.

If another user wants to follow a user and view their messages, the app goes through this process:

* Log in (see above).
* User navigates to the user list and selects a user to follow. The mobile app communicates with Stream API directly to create a [follower relationship](https://getstream.io/get_started/#follow) on their `timeline` feed.
* User views their timeline. The mobile app uses Stream API to retrieve their `timeline` feed, which is composed of all the messages from who they follow.

The code is split between the Android application contained in the `android` directory and the Express backend is in the `backend` directory. See the `README.md` in each folder to see installing and running instructions. If you'd like to follow along with running code, make sure you get both the backend and mobile app running before continuing.

## Prerequisites

Basic knowledge of Node.js (JavaScript) and Android (Kotlin) is required to follow this tutorial. This code is intended to run locally on your machine.

If you'd like to follow along, you'll need an account with [Stream](https://getstream.io/accounts/signup/). Please make sure you can [build a simple Android app](https://developer.android.com/training/basics/firstapp) before embarking on this tutorial. If you haven't done so, make sure you have [Android Studio](https://developer.android.com/studio/install) installed. 

Once you have an account with Stream, you need to set up a development app:

![](images/create-app.png)

You'll need to add the credentials from the Stream app to the source code for it to work. See both the mobile and backend readmes.

Let's get to building!

## User Posts a Status Update

We'll start with a user posting a message.

### Step 1: Log In

In order to communicate with the Stream API, we need a secure frontend token that allows our mobile application to authenticate with Stream directly. This avoids having to proxy all calls through the backend (which is another way of building your application). To do this, we'll need a backend endpoint that uses our Stream account secrets to generate this token. Once we have this token, we don't need the backend to do anything else, since the mobile app has access to the Stream API limited by that user's permissions.

First, we'll be building the log in screen which looks like:

![](images/login.png)

To start, let's lay our form out in Android. In our `activity_main.xml` layout we have a simple `ConstraintLayout` with an `EditText` and `Button`:

```xml
<!-- android/app/src/main/res/layout/activity_main.xml:1 -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
                                                   xmlns:app="http://schemas.android.com/apk/res-auto"
                                                   xmlns:tools="http://schemas.android.com/tools"
                                                   android:layout_width="match_parent"
                                                   android:layout_height="match_parent"
                                                   tools:context="io.getstream.thestream.MainActivity">

    <EditText
        android:id="@+id/user"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:autofillHints="Username"
        android:ems="10"
        android:hint="Username"
        android:inputType="textPersonName"
        app:layout_constraintEnd_toStartOf="@+id/submit"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:ignore="HardcodedText"/>

    <Button
        android:id="@+id/submit"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginEnd="16dp"
        android:text="Login"
        app:layout_constraintBaseline_toBaselineOf="@+id/user"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/user"
        tools:ignore="HardcodedText"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

Let's bind to this layout and respond in `MainActivity`:

```kotlin
// android/app/src/main/java/io/getstream/thestream/MainActivity.kt:16
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
```

*Note: The asyncronous approach in this tutorial is not necessarily the best or most robust approach. It's simply a straightforward way to show async interactions without cluttering the code too much. Please research and pick the best asynchronous solution for your application*


Here we bind to our button and user input. We listen to the submit button and sign into our backend. Since this work is making network calls, we need to do this asynchronously. We use Kotlin coroutines to accomplish this by binding to the [`MainScope`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-main-scope.html). We dispatch our sign in code which tells our `BackendService` to perform two tasks, sign in to backend and get the feed frontend credentials. We'll look at how the `BackendService` accomplishes this in a second. 

Once we have our tokens, we initialize our `FeedService` so we can talk to Stream's API (we'll see this in a second as well). When the user is fully authed and we have our credentials, we start a new activity called `AuthedMainActivity` which is the rest of the application.

Before seeing how we post a message, let's see how we auth and initialize the Stream Feed. First we sign in to the backend via `BackendService.signIn`:

```kotlin
// android/app/src/main/java/io/getstream/thestream/services/BackendService.kt:18
fun signIn(user: String) {
    authToken = post(
        "/v1/users",
        mapOf("user" to user)
    )
        .getString("authToken")
    this.user = user
}

// ...

private fun post(path: String, body: Map<String, Any>, authToken: String? = null): JSONObject {
    val request = Request.Builder()
        .url("$apiRoot${path}")
        .post(JSONObject(body).toString().toRequestBody(JSON))

    if (authToken != null) {
        request.addHeader("Authorization", "Bearer $authToken")
    }

    http.newCall(request.build()).execute().use {
        return JSONObject(it.body!!.string())
    }
}
```

We do a simple `POST` http request to our backend endpoint `/v1/users`, which returns a `backend` `authToken` that allows the mobile application to make further requests against the backend. Since this not a real implemenation of auth, we'll skip the backend code. Please refer to the source if you're curious. Also keep in mind, this token *is not* the Stream token. We need to make another call for that.

Once the user is signed in with our `backend` we can get our feed credentials via `BackendService.getFeedCredentials()`:

```kotlin
// android/app/src/main/java/io/getstream/thestream/services/BackendService.kt:27
data class StreamCredentials(val token: String, val apiKey: String)

fun getFeedCredentials(): StreamCredentials {
    val response = post(
        "/v1/stream-feed-credentials",
        mapOf(),
        authToken
    )

    return StreamCredentials(
        response.getString("token"),
        response.getString("apiKey")
    )
}
```

Similar to before, we `POST` to our `backend` to get our feed credentials. The one difference being we use our `authToken` to authenticate against our backend. Since this backend endpoint creates a Stream user for us, let's take a look:

```javascript
// backend/src/controllers/v1/stream-feed-credentials/stream-feed-credentials.action.js:1
import dotenv from 'dotenv';
import stream from "getstream";

dotenv.config();

exports.streamFeedCredentials = async (req, res) => {
  try {
    const apiKey = process.env.STREAM_API_KEY;
    const apiSecret = process.env.STREAM_API_SECRET;
    const appId = process.env.STREAM_APP_ID;

    const client = stream.connect(apiKey, apiSecret, appId);

    await client.user(req.user).getOrCreate({ name: req.user });
    const token = client.createUserToken(req.user);

    res.status(200).json({ token, apiKey, appId });
  } catch (error) {
    console.log(error);
    res.status(500).json({ error: error.message });
  }
};
```

We use the [Stream JavaScript library](https://github.com/GetStream/stream-js) to create a user (if they don't exist) and generate a [Stream frontend token](https://getstream.io/blog/integrating-with-stream-backend-frontend-options/). We return this token, alongside some api information, back to the Android app.

In the mobile app, we use the returned credentials to intialize our `FeedService` by calling `FeedService.init` in `MainActivity`. Here's the `init`:

```kotlin
// android/app/src/main/java/io/getstream/thestream/services/FeedService.kt:8
object FeedService {
    private lateinit var client: CloudClient
    private lateinit var user: String

    fun init(user: String, credentials: BackendService.StreamCredentials) {
        this.user = user
        client = CloudClient
            .builder(credentials.apiKey, credentials.token, user)
            .build()
    }

    // ...
}
```

The `FeedService` is a singleton (by using Kotlin's [object](https://kotlinlang.org/docs/reference/object-declarations.html)) which stores a `CloudClient` instance. `CloudClient` is a class provided by Stream's Java library. This class is specifically used to provide functionality to client applications via frontend tokens. Stream's Java library contains a normal client for backend applications, so don't get confused on which client to use. The normal client requires private credentials which you don't want to embed in your mobile application!

Now that we're authenticated with Stream, we're ready to post our first message!

### Step 2: Posting a Message

