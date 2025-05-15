package com.amos_tech_code.foodhub.di

import com.amos_tech_code.foodhub.data.SocketService
import com.amos_tech_code.foodhub.location.LocationManager
import com.amos_tech_code.foodhub.ui.feature.orders.details.LocationUpdateSocketRepository
import com.amos_tech_code.foodhub.ui.presentation.feature.orders.LocationUpdateBaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RiderModule {

    @Provides
    fun provideLocationUpdateSocketRepository(
        socketService: SocketService,
        locationManager: LocationManager
    ) : LocationUpdateBaseRepository {
        return LocationUpdateSocketRepository(socketService, locationManager)
    }
}