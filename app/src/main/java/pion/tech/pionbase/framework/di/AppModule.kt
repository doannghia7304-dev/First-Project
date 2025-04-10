package pion.tech.pionbase.framework.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pion.tech.pionbase.R
import pion.tech.pionbase.util.PrefUtil
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideGlide(@ApplicationContext context: Context): RequestManager {
        return Glide.with(context).setDefaultRequestOptions(
            RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .error(R.drawable.ic_error)
        )
    }

    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
            "CLEAN_PREFERENCES",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun providePrefUtil(
        sharedPreferences: SharedPreferences,
    ): PrefUtil {
        return PrefUtil(sharedPreferences)
    }

}
