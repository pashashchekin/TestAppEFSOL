package com.somnium.testappefsol

import android.app.Application
import com.somnium.testappefsol.api.EfsolApi
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        efsolApi = createEfsolApi(interceptor)
    }

    private fun createEfsolApi(interceptor: HttpLoggingInterceptor): EfsolApi {
        val httpClient = OkHttpClient.Builder()

        val client = httpClient.addInterceptor(interceptor).connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS).readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        return retrofit.create(EfsolApi::class.java)
    }

    companion object {
        var efsolApi: EfsolApi? = null
            private set
        private val CONNECT_TIMEOUT = 30
        private val READ_TIMEOUT = 30
    }
}