package com.elliottco.socialnetworkclone.repositories

import com.elliottco.socialnetworkclone.data.entities.User
import com.elliottco.socialnetworkclone.misc.Resource
import com.elliottco.socialnetworkclone.misc.safeCall
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@ActivityScoped
class DefaultAuthRepository : AuthRepository {

    val auth = FirebaseAuth.getInstance()
    val users = FirebaseFirestore.getInstance().collection("users")

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid!!
                val user = User(uid, username)

                users.document(uid).set(user).await()

                Resource.Success(result)
            }
        }
    }

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                Resource.Success(result)
            }
        }
    }
}