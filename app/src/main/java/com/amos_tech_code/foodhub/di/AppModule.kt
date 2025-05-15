package com.amos_tech_code.foodhub.di

import android.content.Context
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.data.SocketService
import com.amos_tech_code.foodhub.data.SocketServiceImpl
import com.amos_tech_code.foodhub.data.remote.NetworkMonitor
import com.amos_tech_code.foodhub.data.remote.NetworkMonitorImpl
import com.amos_tech_code.foodhub.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideOkHttpClient(
        foodHubSession: FoodHubSession,
        @ApplicationContext context: Context
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${foodHubSession.getToken()}")
                    .addHeader("X-Package-Name", context.packageName)
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)  // Connection timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
            .build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient) : Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://fooddeliveryapp-hpg0.onrender.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideFoodApi(retrofit: Retrofit) : FoodApi {
        return retrofit.create(FoodApi::class.java)
    }

    @Provides
    fun provideSession(@ApplicationContext context: Context) : FoodHubSession {
        return FoodHubSession(context)
    }

    @Provides
    fun provideLocationClient(@ApplicationContext context: Context) : FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideLocationManager(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ) : LocationManager {
        return LocationManager(fusedLocationProviderClient, context)
    }


    @Provides
    fun provideSocketService() : SocketService {
        return SocketServiceImpl()
    }

    @Provides
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return NetworkMonitorImpl(context)
    }

}