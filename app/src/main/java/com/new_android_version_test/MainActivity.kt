package com.new_android_version_test

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.new_android_version_test.ui.theme.NewAndroidVersionTestTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private var myService by mutableStateOf(MyService())
    private var isBind by mutableStateOf(false)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService.ServiceBinder
            myService = binder.getService()
            isBind = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBind = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewAndroidVersionTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Screen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding.calculateBottomPadding()),
                    )
                }
            }
        }
    }


    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text( text = "count: ${myService.count}")

            Button(
                onClick = {
                    start()
                }
            ) {
                Text(
                    text = "start",
                )
            }

            Button(
                onClick = {
                    stop()
                }
            ) {
                Text(
                    text = "stop",
                )
            }

        }
    }


    private fun start() {
        if (!isBind) {
            Intent(
                this,
                MyService::class.java
            ).apply {
                action = Actions.START.toString()
                startForegroundService(this)
                bindService(
                    this,
                    connection,
                    Context.BIND_AUTO_CREATE
                )
            }
        }
    }

    private fun stop() {
        if (isBind) {
            unbindService(connection)
            Intent(
                this,
                MyService::class.java
            ).apply {
                action = Actions.STOP.toString()
                startForegroundService(this)
            }

            isBind = false

        }
    }


}

