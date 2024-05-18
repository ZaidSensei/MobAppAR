package com.new_android_version_test

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ServiceComponent::class)
object MainModule {


    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context:Context
    ):NotificationCompat.Builder{

        return NotificationCompat.Builder(
            context,
            Constants.NOTIFICATION_CHANNEL
        )
            .setOngoing(true)
            .setContentTitle("Count running")
            .setContentText("00:00")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
    }



    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ) : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

}