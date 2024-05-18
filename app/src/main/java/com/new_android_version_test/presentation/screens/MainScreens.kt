package com.new_android_version_test.presentation.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.new_android_version_test.domain.Actions
import com.new_android_version_test.download_services.Constants.NAME
import com.new_android_version_test.download_services.Constants.URL
import com.new_android_version_test.download_services.ServiceHelper
import com.new_android_version_test.presentation.MyService
import com.new_android_version_test.util.Constants.SERVICE_TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private var myService by mutableStateOf(MyService())
private var isBind by mutableStateOf(false)

private val connection by mutableStateOf<ServiceConnection>(
    object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService.ServiceBinder
            myService = binder.getService()
            isBind = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBind = false
        }
    }
)


@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val isCancelEnable =
        myService.downloadStatus.status == Actions.RESUME || myService.downloadStatus.status == Actions.START

    val isStartEnable =
        myService.downloadStatus.status == Actions.IDLE || myService.downloadStatus.status == Actions.CANCELLED

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            if (
                myService.downloadStatus.status != Actions.CANCELLED ||
                myService.downloadStatus.status != Actions.FAILED ||
                myService.downloadStatus.status != Actions.IDLE
            ){
                Intent(
                    context,
                    MyService::class.java
                ).apply {
                    context.bindService(this , connection , Context.BIND_AUTO_CREATE)
                }
            }
        }

        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.DESTROYED){
            if (isBind){
                context.unbindService(connection)
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {



        Text(text = "Downloading:$NAME")

        Spacer(modifier = Modifier.height(10.dp))


        LinearProgressIndicator(progress = { myService.downloadStatus.progress / 100f })



        Spacer(modifier = Modifier.height(10.dp))


        Button(
            enabled = isStartEnable,
            onClick = {
                Intent(
                    context,
                    MyService::class.java
                ).apply {
                    putExtra("url", URL)
                    putExtra("name", NAME)

                    action = Actions.START.toString()

                    context.startForegroundService(this)
                    context.bindService(this, connection, Context.BIND_AUTO_CREATE)

                    Log.w(
                        SERVICE_TAG,
                        "isBind:$isBind ",
                    )
                }
            }
        ) {
            Text(text = "Start")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            enabled = isCancelEnable,
            onClick = {
                scope.launch {

                    if (isBind) {
                        context.unbindService(connection)
                    }
                    ServiceHelper.triggerForegroundService(
                        context,
                        actions = Actions.CANCELLED
                    )

                    Log.w(
                        SERVICE_TAG,
                        "isBind:$isBind ",
                    )
                }
            }
        ) {
            Text(text = "Cancel")
        }

    }

}