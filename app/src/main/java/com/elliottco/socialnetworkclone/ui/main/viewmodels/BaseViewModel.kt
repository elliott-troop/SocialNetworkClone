package com.elliottco.socialnetworkclone.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elliottco.socialnetworkclone.data.Post
import com.elliottco.socialnetworkclone.data.entities.User
import com.elliottco.socialnetworkclone.misc.Event
import com.elliottco.socialnetworkclone.misc.Resource
import com.elliottco.socialnetworkclone.repositories.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base class view models that need to load up a list of posts
 */
abstract class BaseViewModel(
        private val repository: MainRepository
) : ViewModel() {

    private val _likePostStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val likePostStatus: LiveData<Event<Resource<Boolean>>> = _likePostStatus

    private val _deletePostStatus = MutableLiveData<Event<Resource<Post>>>()
    val deletePostStatus: LiveData<Event<Resource<Post>>> = _deletePostStatus

    private val _likedByUsers = MutableLiveData<Event<Resource<List<User>>>>()
    val likedByUsers: LiveData<Event<Resource<List<User>>>> = _likedByUsers

    abstract val posts: LiveData<Event<Resource<Post>>>

    abstract fun getPosts(uid: String = "")

    fun getUsers(uids: List<String>) {
        _likedByUsers.postValue(Event(Resource.Loading()))

        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getUsers(uids)
            _likedByUsers.postValue(Event(result))
        }
    }

    fun toggleLikeForPost(post: Post) {
        _likePostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.toggleLikeForPost(post)
            _likePostStatus.postValue(Event(result))
        }
    }

    fun deletePost(post: Post) {
        _deletePostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.deletePost(post)
            _deletePostStatus.postValue(Event(result))
        }
    }
}