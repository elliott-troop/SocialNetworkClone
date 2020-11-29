package com.elliottco.socialnetworkclone.repositories

import android.net.Uri
import com.elliottco.socialnetworkclone.misc.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>
}