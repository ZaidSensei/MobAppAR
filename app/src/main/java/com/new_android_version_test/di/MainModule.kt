package com.new_android_version_test.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.kdownloader.DownloaderConfig
import com.kdownloader.KDownloader
import com.new_android_version_test.R
import com.new_android_version_test.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object MainModule {


    @ServiceScoped
    @Provides
    fun provideKDownloader(
        @ApplicationContext context: Context
    ):KDownloader {
        val config = DownloaderConfig(
            connectTimeOut = 10000,
            readTimeOut = 10000
        )
        return KDownloader.create(context , config)
    }


    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context:Context
    ):NotificationCompat.Builder{

        return NotificationCompat.Builder(
            context,
            Constants.NOTIFICATION_CHANNEL
        )
            .setOngoing(true)
            .setContentTitle("Loading...")
            .setContentText("0")
            .setProgress(100 , 0 , true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
    }



    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ) : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

}