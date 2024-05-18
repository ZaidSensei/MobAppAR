package com.new_android_version_test

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat.Builder
import com.new_android_version_test.Constants.NOTIFICATION_CHANNEL
import com.new_android_version_test.Constants.NOTIFICATION_ID
import com.new_android_version_test.Constants.NOTIFICATION_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyService: Service() {


    @Inject
    lateinit var notificationBuilder: Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    private val binder = ServiceBinder()
    var count by mutableIntStateOf(0)
    var status by mutableStateOf(Actions.IDLE)

    private var job : Job? = null

    override fun onBind(intent: Intent): IBinder = binder



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){
            Actions.START.toString() -> {
                createNotificationChannel()
                startCount()
            }
            Actions.PAUSE.toString() -> pauseCount()
            Actions.STOP.toString() -> stopCount()
        }

        return super.onStartCommand(intent, flags, startId)

    }


    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)
    }

    private fun startCount(){
        startForeground(
            NOTIFICATION_ID,
            notificationBuilder.build(),
        )
        status = Actions.START
        job = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            while (true){
                count ++
                notificationManager.notify(
                    NOTIFICATION_ID,
                    notificationBuilder
                        .setContentTitle("Counting")
                        .setProgress(100 , count , false)
                        .setContentText("$count")
                        .build()
                )
                delay(1000)
            }
        }
    }



    private fun pauseCount(){
        job?.cancel()
        status = Actions.PAUSE
    }

    private fun stopCount(){
        count = 0
        status = Actions.STOP
        job?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    inner class ServiceBinder:Binder(){
        fun getService() = this@MyService
    }

}