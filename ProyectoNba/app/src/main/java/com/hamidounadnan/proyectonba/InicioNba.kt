package com.hamidounadnan.proyectonba

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity


class InicioNba : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        videoFondo()
    }

    fun videoFondo() {

        val videoView = findViewById<VideoView>(R.id.videoFondo)
        val videoPath = "android.resource://${packageName}/${R.raw.inicionba}"
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
        }

    }

    fun nbaBoton(view: View) {


        val intent = Intent(this, PantallaCarga::class.java)
        startActivity(intent)

    }
}