package com.new_android_version_test.domain

data class DownloadInfo(
    val status:Actions = Actions.IDLE,
    val progress:Int = 0,
    val totalSize:Long = 0L,
    val downloadSoFar:Long = 0L,
)
