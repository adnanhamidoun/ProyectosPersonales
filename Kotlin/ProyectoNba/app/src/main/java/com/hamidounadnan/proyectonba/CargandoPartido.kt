package com.hamidounadnan.proyectonba

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


import android.view.animation.AlphaAnimation
import kotlinx.coroutines.*

class CargandoPartido : AppCompatActivity() {

    private lateinit var equipoLocalImage: ImageView
    private lateinit var vsText: ImageView
    private lateinit var equipoVisitanteImage: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var statusTextView: TextView
    private lateinit var continuarButton: ImageButton

    private val messages = listOf("Preparando terreno...", "Saliendo jugadores...", "Empezando partido...")
    private var messageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cargando_partido)


        equipoLocalImage = findViewById(R.id.imagenLocal)
        vsText = findViewById(R.id.imagenVersus)
        equipoVisitanteImage = findViewById(R.id.imagenVisitante)
        progressBar = findViewById(R.id.barraCargar)
        statusTextView = findViewById(R.id.textoCargando)
        continuarButton = findViewById(R.id.continuarBoton)


        continuarButton.visibility = View.INVISIBLE


        val localDrawableId = intent.getIntExtra("equipoLocal", R.drawable.ic_launcher_foreground)
        val visitanteDrawableId = intent.getIntExtra("equipoVisitante", R.drawable.ic_launcher_foreground)


        equipoLocalImage.setImageResource(localDrawableId)
        equipoVisitanteImage.setImageResource(visitanteDrawableId)


        continuarButton.setOnClickListener {
            val intent = Intent(this, MarcadorNba::class.java)
            intent.putExtra("equipoLocal", localDrawableId)
            intent.putExtra("equipoVisitante", visitanteDrawableId)
            startActivity(intent)
            finish()
        }


        iniciarSecuenciaAnimacion()
        cargarProgressBar()
    }

    private fun iniciarSecuenciaAnimacion() {
        // Oculta ambos equipos y el texto "VS" al principio
        equipoLocalImage.isVisible = false
        equipoVisitanteImage.isVisible = false
        vsText.isVisible = false

        // Muestra el equipo local con animaciones
        equipoLocalImage.isVisible = true
        equipoLocalImage.alpha = 0f
        equipoLocalImage.scaleX = 0.5f
        equipoLocalImage.scaleY = 0.5f
        equipoLocalImage.translationX = -300f // Mueve a la izquierda

        equipoLocalImage.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationX(0f) // Regresa a su posición original
            .setDuration(1000)
            .withEndAction {
                // Muestra el "VS" con animación
                vsText.isVisible = true
                vsText.alpha = 0f
                vsText.scaleX = 0.5f
                vsText.scaleY = 0.5f

                vsText.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(800)
                    .setStartDelay(200) // Retraso antes de comenzar la animación
                    .withEndAction {
                        // Muestra el equipo visitante con animación
                        equipoVisitanteImage.isVisible = true
                        equipoVisitanteImage.alpha = 0f
                        equipoVisitanteImage.scaleX = 0.5f
                        equipoVisitanteImage.scaleY = 0.5f
                        equipoVisitanteImage.translationX = 300f // Mueve a la derecha

                        equipoVisitanteImage.animate()
                            .alpha(1f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .translationX(0f) // Regresa a su posición original
                            .setDuration(1000)
                            .setStartDelay(200) // Retraso antes de comenzar la animación
                    }
            }
    }

    private fun cargarProgressBar() {
        // Coroutine para cambiar el mensaje cada 5 segundos con efecto fade in
        CoroutineScope(Dispatchers.Main).launch {
            while (messageIndex < messages.size) {

                statusTextView.text = messages[messageIndex]
                fadeInText(statusTextView)

                messageIndex++
                delay(5000) // Cambia de mensaje cada 5 segundos
            }
        }

        // Animador de la barra de progreso
        val animator = ValueAnimator.ofInt(0, 100).apply {
            duration = 15000

            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                progressBar.progress = progress
            }

            // Al terminar la animación, muestra el botón de continuar y el mensaje "Partido listo"
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    statusTextView.text = "¡Partido listo!"
                    startBlinkingText(statusTextView)
                    continuarButton.visibility = View.VISIBLE
                }
            })
        }

        // Inicia la animación de la barra de progreso
        animator.start()
    }

    // Función para animar el texto con fade in
    private fun fadeInText(textView: TextView) {
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1000 // Duración de la animación de fade in (1 segundo)
            fillAfter = true // Mantener el estado después de la animación
        }
        textView.startAnimation(fadeIn)
    }

    // Función para animar el texto parpadeante
    private fun startBlinkingText(textView: TextView) {
        val blink = AlphaAnimation(0f, 1f).apply {
            duration = 500 // Duración de cada parpadeo
            startOffset = 0
            repeatMode = AlphaAnimation.REVERSE
            repeatCount = AlphaAnimation.INFINITE // Repetir indefinidamente
        }
        textView.startAnimation(blink)
    }
}