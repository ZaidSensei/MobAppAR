package com.new_android_version_test.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import com.kdownloader.KDownloader
import com.new_android_version_test.R
import com.new_android_version_test.domain.Actions
import com.new_android_version_test.domain.DownloadInfo
import com.new_android_version_test.download_services.ServiceHelper
import com.new_android_version_test.util.Constants.NOTIFICATION_CHANNEL
import com.new_android_version_test.util.Constants.NOTIFICATION_ID
import com.new_android_version_test.util.Constants.NOTIFICATION_NAME
import com.new_android_version_test.util.Constants.SERVICE_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyService : Service() {


    @Inject
    lateinit var kDownloader: KDownloader

    @Inject
    lateinit var notificationBuilder: Builder

    @Inject
    lateinit var notificationManager: NotificationManager

//_________________________________________________________________//
//    Bind Service
    private val binder = ServiceBinder()

//________________________________________________________________//

    private var downloadId by mutableIntStateOf(-1)

    var downloadStatus by mutableStateOf(DownloadInfo())

//________________________________________________________________//

    private var job: Job? = null

    private val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

//________________________________________________________________//


    override fun onBind(intent: Intent): IBinder = binder


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {


            Actions.START.toString() -> {

                val getURL = intent.getStringExtra("url")
                val name = intent.getStringExtra("name")


                if (getURL != null && name != null) {

                    createNotificationChannel()
                    startDownload(
                        url = getURL,
                        name = name
                    )

                }
            }

            Actions.PAUSE.toString() -> pause()

            Actions.RESUME.toString() -> resume()

            Actions.CANCELLED.toString() -> {
                if (downloadId != -1) {
                    kDownloader.cancel(downloadId)
                    cancelled()
                }

            }
        }

        return super.onStartCommand(intent, flags, startId)

    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)
    }

    private fun startDownload(
        url: String,
        name: String = "NewFileDownloadingBy-${getString(R.string.app_name)}"
    ) {



        val request = kDownloader.newRequestBuilder(
            url = url,
            dirPath = dir,
            name
        ).build()



        job = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {


            downloadId = kDownloader.enqueue(
                request,
                onStart = {
                    downloadStatus = downloadStatus.copy(
                        status = Actions.START
                    )
                    startForeground(
                        NOTIFICATION_ID,
                        notificationBuilder.build(),
                    )

                    updateNotification(Actions.START)
                },
                onProgress = { progress ->

                    val downloadSoFarKB = request.downloadedBytes / 1024
                    val downloadSoFarMB = downloadSoFarKB / 1024

                    if (downloadStatus.totalSize == 0L) {

                        val sizeKB = request.totalBytes / 1024
                        val sizeMB = sizeKB / 1024

                        downloadStatus = downloadStatus.copy(
                            totalSize = sizeMB,
                        )
                    }

                    if (downloadSoFarMB > downloadStatus.downloadSoFar){
                        downloadStatus = downloadStatus.copy(
                            downloadSoFar = downloadSoFarMB,
                            progress = progress,

                            )

                        notificationManager.notify(
                            NOTIFICATION_ID,
                            notificationBuilder
                                .setContentTitle("Downloading $name")
                                .setProgress(100, progress, false)
                                .setStyle(NotificationCompat.BigTextStyle())
                                .setContentText("Downloaded $downloadSoFarMB MB of ${downloadStatus.totalSize} MB")
                                .build()
                        )
                    }

                },
                onPause = {
                    downloadStatus = downloadStatus.copy(
                        status = Actions.PAUSE
                    )
                    updateNotification(Actions.PAUSE)
                },
                onError = { error ->
                    Log.w(SERVICE_TAG, error)
                    if (error == "Cancelled") {
                        downloadStatus = downloadStatus.copy(
                            status = Actions.CANCELLED
                        )
                    }
                    else{
                        downloadStatus = downloadStatus.copy(
                            status = Actions.FAILED
                        )

                    }
                }
            )
        }



    }


    private fun pause() {
        if (downloadId != -1) {
            kDownloader.pause(downloadId)
        }
    }

    private fun resume(){
        if (downloadId != -1) {
            kDownloader.resume(downloadId)
            downloadStatus = downloadStatus.copy(
                status = Actions.RESUME
            )
            updateNotification(Actions.RESUME)
        }
    }

    private fun cancelled() {
        downloadStatus = downloadStatus.copy(
            status = Actions.CANCELLED
        )
        job?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    private fun updateNotification(actions: Actions){


        val firstAction = when(actions){
            Actions.PAUSE -> {
                NotificationCompat.Action(
                    0,
                    "Resume",
                    ServiceHelper.resumePendingIntent(this)
                )
            }
            else -> {
                NotificationCompat.Action(
                    0,
                    "Pause",
                    ServiceHelper.pausePendingIntent(this)
                )
            }
        }

        val secondAction = NotificationCompat.Action(
            0,
            "Cancel",
            ServiceHelper.cancelPendingIntent(this)
        )

        val thirdAction = NotificationCompat.Action(
            0,
            "Exit",
            ServiceHelper.exitPendingIntent(this)
        )

        notificationBuilder.clearActions()

        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder
                .addAction(firstAction)
                .addAction(secondAction)
                .addAction(thirdAction)
                .build()
        )


    }







    inner class ServiceBinder : Binder() {
        fun getService() = this@MyService
    }

}