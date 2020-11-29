package com.elliottco.socialnetworkclone.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
        // Unique ID of the post
        val id: String = "",

        // ID of the user
        val authorUid: String = "",

        // The username can change, so we don't want to tie that to the post
        @Exclude var authorUsername: String = "",

        // The user's profile picture can change, so we don't want to tie that to the post
        @Exclude var authorProfilePictureUrl: String = "",

        val text: String = "",

        // Image URL of the image attached to the post
        val imageUrl: String = "",

        // Date when the post was created (in milliseconds)
        val date: Long = 0L,

        @Exclude var isLiked: Boolean = false,

        // Used to offset delay for liking a post
        @Exclude var isLiking: Boolean = false,

        // List of user ID's that have liked this post
        val likedBy: List<String> = listOf()
)