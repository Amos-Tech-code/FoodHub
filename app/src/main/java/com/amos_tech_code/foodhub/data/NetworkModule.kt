package com.amos_tech_code.foodhub.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)  // Connection timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
            .build()
    }

    @Provides
    fun provideRetrofit() : Retrofit {

        return Retrofit.Builder()
            .baseUrl("http://192.168.100.8:8080")
            .client(provideOkHttpClient())
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


}