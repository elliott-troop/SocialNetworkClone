package com.elliottco.socialnetworkclone.repositories

import android.net.Uri
import com.elliottco.socialnetworkclone.data.Post
import com.elliottco.socialnetworkclone.data.entities.User
import com.elliottco.socialnetworkclone.misc.Resource

interface MainRepository {

    /**
     * Create post with image and text
     */
    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>

    /**
     * Get list of users
     */
    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    /**
     * Get specific user
     */
    suspend fun getUser(uid: String): Resource<User>

    suspend fun getPostsForFollows(): Resource<List<Post>>
}