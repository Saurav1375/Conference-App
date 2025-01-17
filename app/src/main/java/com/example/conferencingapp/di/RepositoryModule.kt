package com.example.conferencingapp.di

import com.example.conferencingapp.data.repository.ConferenceRepositoryImpl
import com.example.conferencingapp.domain.repository.ConferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindConferenceRepository(
        conferenceRepositoryImpl: ConferenceRepositoryImpl
    ): ConferenceRepository

}