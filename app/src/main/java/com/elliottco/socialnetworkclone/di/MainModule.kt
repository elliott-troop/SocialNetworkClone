package com.elliottco.socialnetworkclone.di

import com.elliottco.socialnetworkclone.repositories.AuthRepository
import com.elliottco.socialnetworkclone.repositories.DefaultAuthRepository
import com.elliottco.socialnetworkclone.repositories.DefaultMainRepository
import com.elliottco.socialnetworkclone.repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object MainModule {

    @ActivityScoped
    @Provides
    fun provideMainRepository() = DefaultMainRepository() as MainRepository
}