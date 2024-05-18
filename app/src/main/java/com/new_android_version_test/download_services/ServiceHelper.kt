package com.new_android_version_test.download_services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.new_android_version_test.presentation.MyService
import com.new_android_version_test.domain.Actions
import com.new_android_version_test.download_services.Constants.CANCELLED_REQUEST_CODE
import com.new_android_version_test.download_services.Constants.EXIT_REQUEST_CODE
import com.new_android_version_test.download_services.Constants.PAUSE_REQUEST_CODE
import com.new_android_version_test.download_services.Constants.RESUME_REQUEST_CODE
import com.new_android_version_test.download_services.Constants.START_REQUEST_CODE

object ServiceHelper {

    

    fun startPendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(
            context,
            MyService::class.java
        ).apply {
            this.action = Actions.START.toString()
        }
        return PendingIntent.getForegroundService(
            context,
            START_REQUEST_CODE,
            clickIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun pausePendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(
            context,
            MyService::class.java
        ).apply {
            this.action = Actions.PAUSE.toString()
        }
        return PendingIntent.getForegroundService(
            context, PAUSE_REQUEST_CODE, clickIntent, PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun resumePendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(
            context,
            MyService::class.java
        ).apply {
            this.action = Actions.RESUME.toString()

        }
        return PendingIntent.getForegroundService(
            context,
            RESUME_REQUEST_CODE,
            clickIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancelPendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(
            context,
            MyService::class.java
        ).apply {
            this.action = Actions.CANCELLED.toString()
        }
        return PendingIntent.getForegroundService(
            context,
            CANCELLED_REQUEST_CODE,
            clickIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun exitPendingIntent(context: Context):PendingIntent {
        val clickIntent =Intent(
            context,
            MyService::class.java
        ).apply {
            this.action = Actions.EXIT.toString()
        }

        return PendingIntent.getForegroundService(
            context,
            EXIT_REQUEST_CODE,
            clickIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }



    fun triggerForegroundService(
        context: Context,
        actions: Actions
    ){
        Intent(
            context,
            MyService::class.java
        ).apply {
            action = actions.toString()
            context.startForegroundService(this)
        }
    }

}