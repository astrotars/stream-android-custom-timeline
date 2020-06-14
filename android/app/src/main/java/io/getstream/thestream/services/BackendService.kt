package io.getstream.thestream.services

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object BackendService {
    private val http = OkHttpClient()
    private val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    private const val apiRoot = "http://10.0.2.2:8080"

    private lateinit var authToken: String
    private lateinit var user: String

    fun signIn(user: String) {
        authToken = post(
            "/v1/users",
            mapOf("user" to user)
        )
            .getString("authToken")
        this.user = user
    }

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

    fun getChatCredentials(): StreamCredentials {
        val response = post(
            "/v1/stream-chat-credentials",
            mapOf(),
            authToken
        )

        return StreamCredentials(
            response.getString("token"),
            response.getString("apiKey")
        )
    }

    fun getUsers(): List<String> {
        val request = Request.Builder()
            .url("$apiRoot/v1/users")
            .addHeader("Authorization", "Bearer $authToken")
            .get()

        http.newCall(request.build()).execute().use { response ->
            val jsonArray = JSONObject(response.body!!.string()).getJSONArray("users")

            return List(jsonArray.length()) { i ->
                jsonArray.get(i).toString()
            }.filterNot { it == user }
        }
    }

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
}