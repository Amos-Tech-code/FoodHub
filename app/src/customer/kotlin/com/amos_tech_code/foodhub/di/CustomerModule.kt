package com.amos_tech_code.foodhub.di

import com.amos_tech_code.foodhub.data.SocketService
import com.amos_tech_code.foodhub.data.repository.CustomerLocationUpdateSocketRepository
import com.amos_tech_code.foodhub.ui.presentation.feature.orders.LocationUpdateBaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CustomerModule {

    @Provides
    fun provideLocationUpdateSocketRepository(
        socketService: SocketService,
    ) : LocationUpdateBaseRepository {
        return CustomerLocationUpdateSocketRepository(socketService)
    }
}