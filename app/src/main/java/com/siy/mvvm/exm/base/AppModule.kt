package com.siy.mvvm.exm.base

import com.siy.mvvm.exm.db.DbModule
import com.siy.mvvm.exm.http.GbdService
import com.siy.mvvm.exm.utils.prefGbd
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Created by Siy on 2019/08/08.
 *
 * @author Siy
 */
@Module(
    includes = [ViewModelModule::class,
        DbModule::class]
)
class AppModule {

    private val token by prefGbd("")

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(55, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(tokenInterceptor)
            .build()
    }

    private val tokenInterceptor by lazy {
        Interceptor { chain ->
            var request = chain.request()
            if (token.isEmpty()) {
                chain.proceed(request)
            } else {
                val builder = request.url()
                    .newBuilder()
                    .addQueryParameter("token", token)
                val httpUrl = builder.build()
                request = request.newBuilder().url(httpUrl).build()
                chain.proceed(request)
            }
        }
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          //  .baseUrl(GbdService.URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideGbdService(): GbdService {
        return retrofit.create(GbdService::class.java)
    }
}