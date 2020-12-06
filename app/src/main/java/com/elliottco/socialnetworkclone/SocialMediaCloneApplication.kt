package com.elliottco.socialnetworkclone

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Starting point for Dagger Hilt
 */
@HiltAndroidApp
class SocialMediaCloneApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}