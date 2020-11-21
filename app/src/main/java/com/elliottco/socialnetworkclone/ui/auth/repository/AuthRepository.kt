package com.elliottco.socialnetworkclone.ui.auth.repository

import com.elliottco.socialnetworkclone.misc.Resource
import com.google.firebase.auth.AuthResult

interface AuthRepository {

    suspend fun register(email: String, username: String, password: String): Resource<AuthResult>
    suspend fun login(email: String, password: String): Resource<AuthResult>
}