package com.example.assignment.di

import com.example.assignment.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideLoggingInterceptor() : HttpLoggingInterceptor {
        return  HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(mHttpLoggingInterceptor : HttpLoggingInterceptor) : OkHttpClient {
        return  OkHttpClient
            .Builder()
            .addInterceptor(mHttpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(mOkHttpClient: OkHttpClient) : Retrofit{
        return Retrofit.Builder().baseUrl("https://picsum.photos/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(mOkHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun providesApiService(retrofit: Retrofit) : ApiService {
        return retrofit.create(ApiService::class.java)
    }
}