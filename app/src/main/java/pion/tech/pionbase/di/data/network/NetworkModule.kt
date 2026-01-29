package pion.tech.pionbase.di.data.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.data.remote.ApiInterface
import pion.tech.pionbase.data.remote.HeaderInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Network layer dependencies
 * - Gson for JSON serialization
 * - OkHttp client with type-safe interceptors (Header, Logging, Chucker)
 * - Retrofit for API communication
 *
 * Using specific types instead of named qualifiers for better type safety
 */
val networkModule =
    module {
        single<Gson> { GsonBuilder().setLenient().create() }

        // Type-safe interceptors - no magic strings
        single<HeaderInterceptor> { HeaderInterceptor() }

        single<HttpLoggingInterceptor> {
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
            }
        }

        single<Cache> {
            val httpCacheDirectory = File(get<Context>().cacheDir, "http-cache")
            Cache(httpCacheDirectory, 10 * 1024 * 1024L) // 10 MB
        }

        single<OkHttpClient> {
            OkHttpClient
                .Builder()
                .cache(get())
                .addInterceptor(get<HeaderInterceptor>())
                .addInterceptor(get<HttpLoggingInterceptor>())
                .addInterceptor(ChuckerInterceptor(get()))
                .connectTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(30L, TimeUnit.SECONDS)
                .build()
        }

        single<Retrofit> {
            Retrofit
                .Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(get())
                .addConverterFactory(GsonConverterFactory.create(get()))
                .build()
        }

        single<ApiInterface> { get<Retrofit>().create(ApiInterface::class.java) }
    }
