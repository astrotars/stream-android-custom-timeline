package io.getstream.thestream.services

import io.getstream.cloud.CloudClient
import io.getstream.core.models.Activity
import io.getstream.core.options.Limit
import java.util.*

object FeedService {
    private lateinit var client: CloudClient
    private lateinit var user: String

    fun init(user: String, credentials: BackendService.StreamCredentials) {
        this.user = user
        client = CloudClient
            .builder(credentials.apiKey, credentials.token, user)
            .build()
    }

    fun follow(otherUser: String) {
        client
            .flatFeed("timeline")
            .follow(client.flatFeed("user", otherUser))
            .join()
    }

    fun timelineFeed(): MutableList<Activity> {
        return client
            .flatFeed("timeline")
            .getActivities(Limit(25))
            .join()
    }

    fun profileFeed(): MutableList<Activity> {
        return client
            .flatFeed("user")
            .getActivities(Limit(25))
            .join()
    }

    fun post(message: String) {
        val feed = client.flatFeed("user")
        feed.addActivity(
            Activity
                .builder()
                .actor("SU:${user}")
                .verb("post")
                .`object`(UUID.randomUUID().toString())
                .extraField("message", message)
                .build()
        ).join()
    }
}