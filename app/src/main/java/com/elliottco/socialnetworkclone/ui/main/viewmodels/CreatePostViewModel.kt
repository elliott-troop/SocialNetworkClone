package com.elliottco.socialnetworkclone.ui.main.viewmodels

import android.content.Context
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elliottco.socialnetworkclone.R
import com.elliottco.socialnetworkclone.misc.Event
import com.elliottco.socialnetworkclone.misc.Resource
import com.elliottco.socialnetworkclone.repositories.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePostViewModel @ViewModelInject constructor(
        private val repository: MainRepository,
        private val applicationContext: Context
) : ViewModel() {

    private val _createPostStatus = MutableLiveData<Event<Resource<Any>>>()
    val createPostStatus: LiveData<Event<Resource<Any>>> = _createPostStatus

    private val _currentImageUri = MutableLiveData<Uri>()
    val currentImageUri: LiveData<Uri> = _currentImageUri

    fun setCurretImageUri(uri: Uri) {
        _currentImageUri.postValue(uri)
    }

    fun createPost(imageUri: Uri, text: String) {
        if(text.isEmpty()) {
            val error = applicationContext.getString(R.string.error_input_empty)
            _createPostStatus.postValue(Event(Resource.Error(error)))
        } else {
            _createPostStatus.postValue(Event(Resource.Loading()))
            viewModelScope.launch(Dispatchers.Main) {
                val result = repository.createPost(imageUri, text)
                _createPostStatus.postValue(Event(Resource.Success(result)))
            }
        }
    }

}