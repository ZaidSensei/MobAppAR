package com.new_android_version_test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) :ViewModel(){

    var isBind by mutableStateOf(false)
    var status by mutableStateOf(Actions.IDLE)

}