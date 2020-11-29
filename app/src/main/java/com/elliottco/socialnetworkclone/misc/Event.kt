package com.elliottco.socialnetworkclone.misc

import androidx.lifecycle.Observer

/**
 * Consumes events so they aren't fired off more than once
 */
class Event<out T>(private val content: T) {

    // Determines if the event has been consumed
    var hasBeenHandled = false
        private set

    // Returns the content if the event has not already been consumed
    fun getContentIfNotHandled(): T? {
        return if (!hasBeenHandled) {
            hasBeenHandled = true
            content
        } else null
    }

    fun peekContent() = content
}

/**
 * Event observer that will handle error, loading, and success observations
 */
class EventObserver<T>(
    private inline val onError: ((String) -> Unit)? = null,
    private inline val onLoading: (() -> Unit)? = null,
    private inline val onSuccess: (T) -> Unit
) : Observer<Event<Resource<T>>> {
    override fun onChanged(t: Event<Resource<T>>?) {

        // Get the content without consuming it
        when(val content = t?.peekContent()) {
            is Resource.Success -> {
                content.data?.let(onSuccess)
            }

            is Resource.Error -> {
                // Consume the error
                t.getContentIfNotHandled()?.let {
                    onError?.let { error ->
                        error(it.message!!)
                    }
                }
            }

            is Resource.Loading -> {
                onLoading?.let { loading ->
                    loading()
                }
            }
        }
    }
}