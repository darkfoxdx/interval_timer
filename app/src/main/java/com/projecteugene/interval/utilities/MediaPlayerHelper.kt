package com.projecteugene.interval.utilities

import android.content.Context
import android.media.MediaPlayer
import com.projecteugene.interval.R

class MediaPlayerHelper(context: Context) {
    private val mediaPlayer = MediaPlayer.create(context, R.raw.tone1)
    fun start() {
        mediaPlayer.start()
    }
    fun release() {
        mediaPlayer.release()
    }
}