package com.new_android_version_test.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.new_android_version_test.domain.Actions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) :ViewModel(){

    var isBind by mutableStateOf(false)
    var status by mutableStateOf(Actions.IDLE)

}