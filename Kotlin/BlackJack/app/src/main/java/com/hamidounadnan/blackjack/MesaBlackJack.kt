package com.hamidounadnan.blackjack

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hamidounadnan.Objects.Baraja
import com.hamidounadnan.Objects.Carta
import kotlinx.coroutines.*

class MesaBlackJack : AppCompatActivity() {

    private var currentCardIndexJugador = 0
    private var currentCardIndexCrupier = 0
    private lateinit var cartasJugador: List<ImageView>
    private lateinit var cartasCrupier: List<ImageView>
    private var turnoJugador = true
    private var baraja = Baraja()
    private var contadorJugador = 0
    private var contadorCrupier = 0
    private lateinit var contadorTextoJugador: TextView
    private lateinit var contadorTextoCrupier: TextView
    private var esUnAsJugador = false
    private var esUnAsCrupier = false
    private lateinit var btnReiniciarPartida: Button
    private lateinit var btnPedirCarta: Button
    private lateinit var btnPlantarse: Button
    private var blackJackJugador = false
    private var blackJackCrupier = false
    private lateinit var cartaCrupier: Carta
    private lateinit var cartaJugador: Carta
    private var loop = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesa_black_jack)
        contadorTextoJugador = findViewById(R.id.textView)
        contadorTextoCrupier = findViewById(R.id.textView2)
        btnReiniciarPartida = findViewById<Button>(R.id.reiniciarPartidaButton)
        btnPedirCarta = findViewById<Button>(R.id.buttonPedirCarta)
        btnPlantarse = findViewById<Button>(R.id.buttonPlantarse)
        btnReiniciarPartida.visibility = View.INVISIBLE
        btnPedirCarta.visibility = View.INVISIBLE
        btnPlantarse.visibility = View.INVISIBLE
      

        cartasJugador = listOf(
            findViewById<ImageView>(R.id.cartaJugador1),
            findViewById<ImageView>(R.id.cartaJugador2),
            findViewById<ImageView>(R.id.cartaJugador3),
            findViewById<ImageView>(R.id.cartaJugador4),
            findViewById<ImageView>(R.id.cartaJugador5),
            findViewById<ImageView>(R.id.cartaJugador6),
            findViewById<ImageView>(R.id.cartaJugador7),
            findViewById<ImageView>(R.id.cartaJugador8),
            findViewById<ImageView>(R.id.cartaJugador9),
            findViewById<ImageView>(R.id.cartaJugador10)
        )

        cartasCrupier = listOf(
            findViewById<ImageView>(R.id.cartaCrupier1),
            findViewById<ImageView>(R.id.cartaCrupier2),
            findViewById<ImageView>(R.id.cartaCrupier3),
            findViewById<ImageView>(R.id.cartaCrupier4),
            findViewById<ImageView>(R.id.cartaCrupier5),
            findViewById<ImageView>(R.id.cartaCrupier6),
            findViewById<ImageView>(R.id.cartaCrupier7),
            findViewById<ImageView>(R.id.cartaCrupier8),
        )
        // Hacer invisibles las cartas del jugador
        cartasJugador.forEach { it.visibility = View.INVISIBLE }

        // Hacer invisibles las cartas del crupier
        cartasCrupier.forEach { it.visibility = View.INVISIBLE }


        // Baraja (punto de partida)
        val barajaCartas = findViewById<ImageView>(R.id.barajaCartas)

        // Coordenadas iniciales de la baraja
        val location = IntArray(2)
        barajaCartas.getLocationOnScreen(location)
        val startX = location[0].toFloat() + 740f // Ajuste en X
        val startY = location[1].toFloat() + 280f // Ajuste en Y

        // Coordenadas base de las cartas del jugador
        val baseXJugador = 75f   // Coordenada X inicial para la primera carta del jugador
        val baseYJugador = 1310f // Coordenada Y para las cartas del jugador
        val offsetX = 76f        // Incremento horizontal entre cada carta

        // Coordenadas base de las cartas del crupier
        val baseXCrupier = 75f   // Coordenada X inicial para la primera carta del crupier
        val baseYCrupier = 500f  // Coordenada Y más arriba para las cartas del crupier

        // Repartir las primeras cartas alternando con un retraso de 3 segundos
        repartirCartasIniciales(
            baseXJugador,
            baseYJugador,
            baseXCrupier,
            baseYCrupier,
            startX,
            startY,
            offsetX
        )

        // Configurar el botón para pedir carta

        btnPedirCarta.setOnClickListener {
            if (turnoJugador) {
                if (currentCardIndexJugador < cartasJugador.size) {
                    val endX = baseXJugador + (offsetX * currentCardIndexJugador)
                    val endY = baseYJugador
                    animarCarta(
                        barajaCartas,
                        cartasJugador[currentCardIndexJugador],
                        startX,
                        startY,
                        endX - startX,
                        endY - startY
                    )
                    cartaJugador = baraja.obtenerCartaAleatoria()
                    cartasJugador.get(currentCardIndexJugador)
                        .setImageResource(cartaJugador.getImagenResId())
                    contadorJugador += cartaJugador.getValor()
                    contadorTextoJugador.text = contadorJugador.toString()
                    if (cartaJugador.getValor() == 11) {
                        esUnAsJugador = true
                    }
                    if (contadorJugador > 21) {
                        if (esUnAsJugador == true) {
                            contadorJugador -= 10
                            contadorTextoJugador.text = contadorJugador.toString()
                            esUnAsJugador = false
                        } else {

                            contadorTextoJugador.setTextColor(Color.RED)
                            contadorTextoCrupier.setTextColor(Color.GREEN)
                            Snackbar.make(findViewById(android.R.id.content), "Jugador se pasa. Crupier Gana", Snackbar.LENGTH_LONG)
                                .setBackgroundTint(Color.RED)
                                .setTextColor(Color.BLACK)
                                .show()
                            btnPlantarse.visibility = View.INVISIBLE
                            btnReiniciarPartida.visibility = View.VISIBLE
                            turnoJugador = false
                        }

                    }
                    currentCardIndexJugador++

                } else {
                    Toast.makeText(
                        this,
                        "No hay más cartas disponibles para el jugador",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Configurar el botón para plantarse

        btnPlantarse.setOnClickListener {
            turnoJugador = false
            btnPlantarse.visibility = View.INVISIBLE
            btnPedirCarta.visibility = View.INVISIBLE
            repartirCartasCrupierConRetraso(baseXCrupier, baseYCrupier, startX, startY, offsetX)


        }

        btnReiniciarPartida.setOnClickListener {
            btnPlantarse.visibility = View.INVISIBLE
            btnPedirCarta.visibility = View.INVISIBLE
            cartasJugador.forEach { it.visibility = View.INVISIBLE }
            cartasCrupier.forEach { it.visibility = View.INVISIBLE }
            currentCardIndexJugador = 0
            currentCardIndexCrupier = 0
            turnoJugador = true
            esUnAsJugador = false
            esUnAsCrupier = false
            contadorCrupier = 0
            contadorJugador = 0
            contadorTextoCrupier.text = contadorCrupier.toString()
            contadorTextoJugador.text = contadorJugador.toString()
            cartasCrupier.get(1).setImageResource(R.drawable.cartareversed)
            btnReiniciarPartida.visibility = View.INVISIBLE
            btnPlantarse.visibility = View.INVISIBLE
            btnPedirCarta.visibility = View.INVISIBLE
            loop = 0
            contadorTextoJugador.setTextColor(Color.WHITE)
            contadorTextoCrupier.setTextColor(Color.WHITE)
            repartirCartasIniciales(
                baseXJugador,
                baseYJugador,
                baseXCrupier,
                baseYCrupier,
                startX,
                startY,
                offsetX
            )


        }
    }

    private fun repartirCartasIniciales(
        baseXJugador: Float,
        baseYJugador: Float,
        baseXCrupier: Float,
        baseYCrupier: Float,
        startX: Float,
        startY: Float,
        offsetX: Float
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            cartaJugador = baraja.obtenerCartaAleatoria()
            cartasJugador.get(currentCardIndexJugador)
                .setImageResource(cartaJugador.getImagenResId())
            contadorJugador += cartaJugador.getValor()


            // Carta para el jugador
            val endXJugador = baseXJugador + (offsetX * currentCardIndexJugador)
            val endYJugador = baseYJugador
            animarCarta(
                cartasJugador[0],
                cartasJugador[currentCardIndexJugador],
                startX,
                startY,
                endXJugador - startX,
                endYJugador - startY
            )
            currentCardIndexJugador++
            delay(1000)
            contadorTextoJugador.text = contadorJugador.toString()
            if (cartaJugador.getValor() == 11) {
                esUnAsJugador = true
            }


            cartaCrupier = baraja.obtenerCartaAleatoria()
            cartasCrupier.get(currentCardIndexCrupier)
                .setImageResource(cartaCrupier.getImagenResId())
            contadorCrupier += cartaCrupier.getValor()
            // Carta para el crupier
            val endXCrupier = baseXCrupier + (offsetX * currentCardIndexCrupier)
            val endYCrupier = baseYCrupier
            animarCarta(
                cartasCrupier[0],
                cartasCrupier[currentCardIndexCrupier],
                startX,
                startY,
                endXCrupier - startX,
                endYCrupier - startY
            )
            currentCardIndexCrupier++
            delay(1000)
            contadorTextoCrupier.text = contadorCrupier.toString()
            if (cartaCrupier.getValor() == 11) {
                esUnAsCrupier = true
            }

            cartaJugador = baraja.obtenerCartaAleatoria()
            cartasJugador.get(currentCardIndexJugador)
                .setImageResource(cartaJugador.getImagenResId())
            contadorJugador += cartaJugador.getValor()

            // Segunda carta para el jugador
            val endXJugador2 = baseXJugador + (offsetX * currentCardIndexJugador)
            val endYJugador2 = baseYJugador
            animarCarta(
                cartasJugador[0],
                cartasJugador[currentCardIndexJugador],
                startX,
                startY,
                endXJugador2 - startX,
                endYJugador2 - startY
            )
            currentCardIndexJugador++
            delay(1000)
            contadorTextoJugador.text = contadorJugador.toString()
            if (cartaJugador.getValor() == 11) {
                esUnAsJugador = true
            }
            if (contadorJugador > 21) {
                contadorJugador -= 10
            }


            // Segunda carta para el crupier
            val endXCrupier2 = baseXCrupier + (offsetX * currentCardIndexCrupier)
            val endYCrupier2 = baseYCrupier
            animarCarta(
                cartasCrupier[0],
                cartasCrupier[currentCardIndexCrupier],
                startX,
                startY,
                endXCrupier2 - startX,
                endYCrupier2 - startY
            )
            cartaCrupier = baraja.obtenerCartaAleatoria()
            if (contadorCrupier + cartaCrupier.getValor() == 21) {
                cartasCrupier.get(currentCardIndexCrupier)
                    .setImageResource(cartaCrupier.getImagenResId())
                contadorTextoCrupier.text = "21"
                contadorCrupier += cartaCrupier.getValor()
            }

            if (cartaCrupier.getValor() == 11) {
                esUnAsCrupier = true
            }
            if (contadorCrupier > 21) {
                contadorCrupier -= 10
            }
            if (contadorCrupier == 21 && contadorJugador == 21) {
                Toast.makeText(
                    this@MesaBlackJack,
                    "Empate",
                    Toast.LENGTH_SHORT
                ).show()
                btnReiniciarPartida.visibility = View.VISIBLE
            } else if (contadorJugador == 21) {
                Snackbar.make(findViewById(android.R.id.content), "Blackjack por parte Jugador", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.BLACK)
                    .setTextColor(Color.YELLOW)
                    .show()
                contadorTextoCrupier.setTextColor(Color.RED)
                contadorTextoJugador.setTextColor(Color.GREEN)
                btnReiniciarPartida.visibility = View.VISIBLE
            } else if (contadorCrupier == 21) {
                Snackbar.make(findViewById(android.R.id.content), "Blackjack por parte Crupier", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.BLACK)
                    .setTextColor(Color.YELLOW)
                    .show()
                contadorTextoCrupier.setTextColor(Color.GREEN)
                contadorTextoJugador.setTextColor(Color.RED)
                btnReiniciarPartida.visibility = View.VISIBLE
            } else {
                btnPlantarse.visibility = View.VISIBLE
                btnPedirCarta.visibility = View.VISIBLE
            }


        }
    }

    private fun repartirCartasCrupierConRetraso(
        baseXCrupier: Float,
        baseYCrupier: Float,
        startX: Float,
        startY: Float,
        offsetX: Float
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            while (contadorCrupier < 17) {

                if (contadorCrupier < 17) {
                    val endX = baseXCrupier + (offsetX * currentCardIndexCrupier)
                    val endY = baseYCrupier
                    animarCarta(
                        cartasCrupier[0],
                        cartasCrupier[currentCardIndexCrupier],
                        startX,
                        startY,
                        endX - startX,
                        endY - startY
                    )
                    if (loop > 0) {
                        cartaCrupier = baraja.obtenerCartaAleatoria()
                    }

                    cartasCrupier.get(currentCardIndexCrupier)
                        .setImageResource(cartaCrupier.getImagenResId())
                    contadorCrupier += cartaCrupier.getValor()
                    println(contadorCrupier)
                    contadorTextoCrupier.text = contadorCrupier.toString()
                    if (cartaCrupier.getValor() == 11) {
                        esUnAsCrupier = true
                    }

                    currentCardIndexCrupier++
                    delay(1000)
                }
                if (contadorCrupier > 21 && esUnAsCrupier == true) {
                    esUnAsCrupier = false
                    contadorCrupier -= 10
                }
                loop++
            }
            btnReiniciarPartida.visibility = View.VISIBLE


            if (contadorCrupier > contadorJugador && contadorCrupier < 22) {
                Thread.sleep(1000)
                contadorTextoCrupier.setTextColor(Color.GREEN)
                contadorTextoJugador.setTextColor(Color.RED)
                Snackbar.make(findViewById(android.R.id.content), "El crupier gana con $contadorCrupier puntos.", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE)
                    .show()
            } else if (contadorCrupier == contadorJugador) {
                Thread.sleep(1000)
                contadorTextoJugador.setTextColor(Color.YELLOW)
                contadorTextoCrupier.setTextColor(Color.YELLOW)
                Snackbar.make(findViewById(android.R.id.content), "¡Empate! Ambos tienen $contadorJugador puntos.", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.YELLOW)
                    .setTextColor(Color.BLACK)
                    .show()
            } else {
                Thread.sleep(1000)
                contadorTextoJugador.setTextColor(Color.GREEN)
                contadorTextoCrupier.setTextColor(Color.RED)
                Snackbar.make(findViewById(android.R.id.content), "Jugador gana  con $contadorJugador puntos.", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.GREEN)
                    .setTextColor(Color.BLACK)
                    .show()
            }

        }


    }

    private fun animarCarta(
        fromView: View,
        toView: View,
        startX: Float,
        startY: Float,
        xOffset: Float,
        yOffset: Float
    ) {
        toView.visibility = View.VISIBLE

        val animX = ObjectAnimator.ofFloat(toView, "x", startX, startX + xOffset)
        val animY = ObjectAnimator.ofFloat(toView, "y", startY, startY + yOffset)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animX, animY)
        animatorSet.duration = 1000
        animatorSet.start()
    }
}


