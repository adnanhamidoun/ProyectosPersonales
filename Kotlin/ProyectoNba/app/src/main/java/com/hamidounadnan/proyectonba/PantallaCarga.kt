package com.hamidounadnan.proyectonba

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.animation.ValueAnimator
import android.content.Intent
import android.widget.ProgressBar
import android.widget.TextView


class PantallaCarga : AppCompatActivity() {
    val a: ValueAnimator = ValueAnimator.ofInt(0, 100)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carga_pantalla)
        cargarProgressBar()
    }

    private fun cargarProgressBar() {
        val barraCargar = findViewById<ProgressBar>(R.id.barraCargar)
        val progresoText = findViewById<TextView>(R.id.textoCargando)

        ValueAnimator.ofInt(0, 100).apply {
            duration = 5000
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                barraCargar.progress = progress
                progresoText.text = "$progress%"
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    startActivity(Intent(this@PantallaCarga, SeleccionarEquipos::class.java))
                }
            })
            start()
        }
    }
}
