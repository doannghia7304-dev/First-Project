package pion.tech.pionbase.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
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

private const val CACHE_DIR_NAME = "http-cache"
private const val CACHE_SIZE_MB = 10L
private const val CACHE_SIZE_BYTES = CACHE_SIZE_MB * 1024 * 1024
private const val TIMEOUT_SECONDS = 30L

val networkModule =
    module {
        single<Gson> { GsonBuilder().setStrictness(Strictness.LENIENT).create() }

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
            val httpCacheDirectory = File(get<Context>().cacheDir, CACHE_DIR_NAME)
            Cache(httpCacheDirectory, CACHE_SIZE_BYTES)
        }

        single<OkHttpClient> {
            OkHttpClient
                .Builder()
                .cache(get())
                .addInterceptor(get<HeaderInterceptor>())
                .addInterceptor(get<HttpLoggingInterceptor>())
                .addInterceptor(ChuckerInterceptor(get()))
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
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
