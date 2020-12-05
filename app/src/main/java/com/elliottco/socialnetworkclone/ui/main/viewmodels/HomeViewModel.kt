package com.elliottco.socialnetworkclone.ui.main.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elliottco.socialnetworkclone.data.Post
import com.elliottco.socialnetworkclone.misc.Event
import com.elliottco.socialnetworkclone.misc.Resource
import com.elliottco.socialnetworkclone.repositories.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
        private val repository: MainRepository
) : BasePostViewModel(repository) {

    private val _posts = MutableLiveData<Event<Resource<List<Post>>>>()
    override val posts: LiveData<Event<Resource<List<Post>>>>
        get() = _posts

    init {
        getPosts()
    }

    // Show list of posts from users the current user is following
    override fun getPosts(uid: String) {
        _posts.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getPostsFromFollowing()
            _posts.postValue(Event(result))
        }
    }
}