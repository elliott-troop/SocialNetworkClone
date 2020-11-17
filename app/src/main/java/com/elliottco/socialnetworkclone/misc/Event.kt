package com.elliottco.socialnetworkclone.misc

/**
 * Consumes events so they aren't fired off more than once
 */
class Event<out T>(private val content: T) {

    // Determines if the event has been consumed
    var hasBeenHandled = false
        private set

    //
    fun getContentIfNotHandled(): T? {
        return if (!hasBeenHandled) {
            hasBeenHandled = true
            content
        } else null
    }

    fun peekContent() = content
}