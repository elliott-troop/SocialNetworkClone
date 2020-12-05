package com.elliottco.socialnetworkclone.repositories

import android.net.Uri
import com.elliottco.socialnetworkclone.data.Post
import com.elliottco.socialnetworkclone.data.entities.User
import com.elliottco.socialnetworkclone.misc.Resource
import com.elliottco.socialnetworkclone.misc.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import java.util.*

@ActivityScoped
class DefaultMainRepository : MainRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val users = firestore.collection("users")
    private val posts = firestore.collection("posts")
    private val comments = firestore.collection("comments")

    override suspend fun createPost(imageUri: Uri, text: String): Resource<Any> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val uid = auth.uid!!
                val postId = UUID.randomUUID().toString()
                val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
                val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()

                val post = Post(
                        id = postId,
                        authorUid = uid,
                        text = text,
                        imageUrl = imageUrl,
                        date = System.currentTimeMillis()
                )

                posts.document(postId).set(post).await()
                Resource.Success(Any())
            }
        }
    }

    override suspend fun getUsers(uids: List<String>): Resource<List<User>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                // Query users collection
                val usersList = users.whereIn("uid", uids).orderBy("username").get().await()
                        .toObjects(User::class.java)
                Resource.Success(usersList)
            }
        }
    }

    override suspend fun getUser(uid: String): Resource<User> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val searchedUser = users.document(uid).get().await().toObject(User::class.java)
                        ?: throw IllegalStateException()

                val currentUserUid = FirebaseAuth.getInstance().uid!!
                val currentUser = users.document(currentUserUid).get().await().toObject(User::class.java)
                        ?: throw IllegalStateException()

                // Set isFollowing to true if the searched user is followed by the current user
                searchedUser.isFollowing = uid in currentUser.follows

                Resource.Success(searchedUser)
            }
        }
    }

    override suspend fun getPostsFromFollowing(): Resource<List<Post>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val currentUserUid = FirebaseAuth.getInstance().uid!!
                val followedUsers = getUser(currentUserUid).data!!.follows

                // Get posts ordered by date
                val allPosts = posts.whereIn("authorUid", followedUsers)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .await()
                        .toObjects(Post::class.java)
                        .onEach { post ->
                            val author = getUser(post.authorUid).data!!
                            post.authorProfilePictureUrl = author.profilePictureUrl
                            post.authorUsername = author.username
                            post.isLiked = currentUserUid in post.likedBy
                        }
                Resource.Success(allPosts)
            }
        }
    }

    override suspend fun toggleLikeForPost(post: Post): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            safeCall {
                var isLiked = false
                firestore.runTransaction { transaction ->
                    val currentUserUid = FirebaseAuth.getInstance().uid!!
                    val postResult = transaction.get(posts.document(post.id))
                    val currentLikes = postResult.toObject(Post::class.java)?.likedBy
                            ?: listOf()
                    transaction.update(
                            posts.document(post.id),
                            "likedBy",
                            if(currentUserUid in currentLikes)
                                currentLikes - currentUserUid
                            else {
                                isLiked = true
                                currentLikes + currentUserUid
                            }
                    )
                }.await()

                Resource.Success(isLiked)
            }
        }
    }

    override suspend fun deletePost(post: Post): Resource<Post> {
        return withContext(Dispatchers.IO) {
            safeCall {
                // Delete the post in Firestore
                posts.document(post.id).delete().await()

                // Delete the image from Cloud Storage
                storage.getReferenceFromUrl(post.imageUrl).delete().await()

                Resource.Success(post)
            }
        }
    }

    override suspend fun getPostsForUser(uid: String): Resource<List<Post>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val profilePosts = posts.whereEqualTo("authorUid", uid)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .await()
                        .toObjects(Post::class.java)
                        .onEach { post ->
                            val author = getUser(post.authorUid).data!!
                            post.authorProfilePictureUrl = author.profilePictureUrl
                            post.authorUsername = author.username
                            post.isLiked = uid in post.likedBy
                        }
                Resource.Success(profilePosts)
            }
        }
    }

    override suspend fun toggleFollowForUser(uid: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {

            var isFollowing = false

            firestore.runTransaction { transaction ->
                val currentUid = auth.uid!!
                val currentUser = transaction.get(users.document(currentUid)).toObject(User::class.java)!!
                isFollowing = uid in currentUser.follows

                val newFollows = if(isFollowing) currentUser.follows - uid else currentUser.follows + uid
                transaction.update(users.document(currentUid), "follows", newFollows)
            }.await()

            Resource.Success(!isFollowing)
        }
    }
}