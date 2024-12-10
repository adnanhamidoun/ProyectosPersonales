package com.hamidounadnan.proyectonba

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class MarcadorNba : AppCompatActivity() {
    private lateinit var equipoLocalImage: ImageView
    private lateinit var equipoVisitanteImage: ImageView
    private var puntuacionLocal = 0
    private var puntuacionVisitante = 0
    private var esLocal = true
    private var contadorCuartos = false
    private lateinit var minutosDecena: ImageView
    private lateinit var minutosUnidad: ImageView
    private lateinit var segundosDecena: ImageView
    private lateinit var segundosUnidad: ImageView
    private lateinit var temporizador: CountDownTimer
    private var tiempoRestante: Long = 600000 // 10 minutos en milisegundos
    private val intervalo: Long = 1000 // Intervalo de actualización en milisegundos (1 segundo)
    private var temporizadorActivo = false
    private lateinit var unidadLocal: ImageView
    private lateinit var decenaLocal: ImageView
    private lateinit var unidadVisitante: ImageView
    private lateinit var decenaVisitante: ImageView
    private var numeroCuarto = 1
    private lateinit var faltasPersonalesLocal: List<ImageView>
    private lateinit var faltasPersonalesVisitante: List<ImageView>

    private lateinit var botonCambiarEquipo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marcador_nba)

        // Configuración de imágenes de los equipos
        equipoLocalImage = findViewById(R.id.imagenlocal)
        equipoVisitanteImage = findViewById(R.id.imagenvisitante)
        equipoLocalImage.setImageResource(
            intent.getIntExtra(
                "equipoLocal",
                R.drawable.ic_launcher_foreground
            )
        )
        equipoVisitanteImage.setImageResource(
            intent.getIntExtra(
                "equipoVisitante",
                R.drawable.ic_launcher_foreground
            )
        )

        // Asigna los ImageView para el marcador
        unidadLocal = findViewById(R.id.localUnidad)
        decenaLocal = findViewById(R.id.localDecena)
        unidadVisitante = findViewById(R.id.visitanteUnidad)
        decenaVisitante = findViewById(R.id.visitanteDecena)


        inicializarFaltasPersonales()
        botonCambiarEquipo = findViewById(R.id.buttoncambiaVisitanteLocal)

        findViewById<Button>(R.id.buttonSumar1punto).setOnClickListener { actualizarMarcador(1) }
        findViewById<Button>(R.id.buttonSumar2puntos).setOnClickListener { actualizarMarcador(2) }
        findViewById<Button>(R.id.buttonSumar3puntos).setOnClickListener { actualizarMarcador(3) }
        findViewById<Button>(R.id.buttonRestar1punto).setOnClickListener { actualizarMarcador(-1) }
        // Inicialización de los ImageViews para los dígitos del temporizador
        minutosDecena = findViewById(R.id.decenaMinuto)
        minutosUnidad = findViewById(R.id.unidadMinuto)
        segundosDecena = findViewById(R.id.decenaSegundo)
        segundosUnidad = findViewById(R.id.unidadSegundo)
        val textViewLocal = findViewById<TextView>(R.id.textViewLocal)
        textViewLocal.setTextColor(Color.parseColor("#FF9800"))





        inicializarTemporizador(tiempoRestante)
        actualizarTemporizadorDisplay(tiempoRestante)
    }

    // Inicialización de faltas personales
    private fun inicializarFaltasPersonales() {
        faltasPersonalesLocal = listOf(
            findViewById(R.id.localFaltasImage1),
            findViewById(R.id.localFaltasImage2),
            findViewById(R.id.localFaltasImage3),
            findViewById(R.id.localFaltasImage4),
            findViewById(R.id.localFaltasImage5)
        )
        faltasPersonalesVisitante = listOf(
            findViewById(R.id.VisitanteFaltasImage1),
            findViewById(R.id.VisitanteFaltasImage2),
            findViewById(R.id.VisitanteFaltasImage3),
            findViewById(R.id.VisitanteFaltasImage4),
            findViewById(R.id.VisitanteFaltasImage5)
        )

        // Oculta todas las imágenes de faltas personales al iniciar
        for (falta in faltasPersonalesLocal + faltasPersonalesVisitante) {
            falta.visibility = View.INVISIBLE
        }
    }

    // Actualización del marcador
    private fun actualizarMarcador(puntos: Int) {
        if (esLocal) {
            puntuacionLocal = (puntuacionLocal + puntos).coerceAtLeast(0)
            actualizarVistaMarcador(puntuacionLocal, decenaLocal, unidadLocal)
        } else {
            puntuacionVisitante = (puntuacionVisitante + puntos).coerceAtLeast(0)
            actualizarVistaMarcador(puntuacionVisitante, decenaVisitante, unidadVisitante)
        }
    }

    // Actualización de imágenes de los dígitos
    private fun actualizarVistaMarcador(
        puntuacion: Int,
        decenaView: ImageView,
        unidadView: ImageView
    ) {
        val decenas = puntuacion / 10
        val unidad = puntuacion % 10
        decenaView.setImageResource(getImageResourceForDigit(decenas))
        unidadView.setImageResource(getImageResourceForDigit(unidad))
    }

    // Obtiene el recurso de imagen según el dígito
    private fun getImageResourceForDigit(digit: Int): Int {
        return when (digit) {
            0 -> R.drawable.cerodigital
            1 -> R.drawable.unodigital
            2 -> R.drawable.dosdigital
            3 -> R.drawable.tresdigital
            4 -> R.drawable.cuatrodigital
            5 -> R.drawable.cincodigital
            6 -> R.drawable.seisdigital
            7 -> R.drawable.sietedigital
            8 -> R.drawable.ochodigital
            9 -> R.drawable.nuevedigital
            else -> R.drawable.cerodigital
        }
    }

    // Cambiar el equipo (local o visitante)
    fun cambiarVisitanteLocal(view: View) {
        val textViewLocal = findViewById<TextView>(R.id.textViewLocal)
        val textViewVisitante = findViewById<TextView>(R.id.textViewVisitante)
        botonCambiarEquipo.text = if (esLocal) "Local" else "Visitante"
        esLocal = !esLocal
        if (esLocal) {
            textViewLocal.setTextColor(Color.parseColor("#FF9800"))
            textViewVisitante.setTextColor(Color.parseColor("#B0BEC5"))
        } else {
            textViewVisitante.setTextColor(Color.parseColor("#FF9800"))
            textViewLocal.setTextColor(Color.parseColor("#B0BEC5"))
        }

    }

    // Sumar falta personal
    fun sumarFaltaPersonal(view: View) {
        val faltasPersonales = if (esLocal) faltasPersonalesLocal else faltasPersonalesVisitante
        for (falta in faltasPersonales) {
            if (!falta.isVisible) {
                falta.visibility = View.VISIBLE
                break
            }
        }
    }

    // Inicializa un temporizador con el tiempo dado (en milisegundos)
    fun inicializarTemporizador(tiempo: Long) {
        // Crea un objeto CountDownTimer con el tiempo especificado y un intervalo de actualización
        temporizador = object : CountDownTimer(tiempo, intervalo) {

            // Método que se llama en cada "tick" (cada segundo, según el intervalo)
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = millisUntilFinished // Actualiza el tiempo restante
                actualizarTemporizadorDisplay(tiempoRestante) // Actualiza la pantalla con el tiempo restante
            }

            // Método que se llama cuando el temporizador llega a cero
            override fun onFinish() {
                temporizadorActivo = false // Marca el temporizador como inactivo
                actualizarTemporizadorDisplay(tiempoRestante) // Muestra "00:00" al finalizar
            }
        }
    }

    fun iniciarOReanudarTemporizador(view: View) {
        contadorCuartos = true
        if (!temporizadorActivo) {
            temporizadorActivo = true
            inicializarTemporizador(tiempoRestante)
            temporizador.start()
        } else {
            // Pausa el temporizador
            temporizador.cancel()
            temporizadorActivo = false
        }
    }

    // Reinicia el temporizador a su valor inicial de 10 minutos
    fun reiniciarTemporizador(view: View) {
        sumarCuartos()
        contadorCuartos = false
        temporizador.cancel() // Detiene el temporizador actual
        tiempoRestante = 600000 // Reinicia el tiempo a 10 minutos (600,000 ms)
        actualizarTemporizadorDisplay(tiempoRestante) // Actualiza la pantalla con el nuevo tiempo
        temporizadorActivo = false // Marca el temporizador como inactivo
        reiniciarFaltasPersonales() // Reinicia las faltas personales a cero
    }

    // Reinicia las imágenes de faltas personales, haciéndolas invisibles
    fun reiniciarFaltasPersonales() {
        // Para cada falta personal del equipo local, la oculta
        for (falta in faltasPersonalesLocal) {
            falta.visibility =
                View.INVISIBLE // También puedes usar View.GONE si no quieres que ocupen espacio
        }

        // Para cada falta personal del equipo visitante, la oculta
        for (falta in faltasPersonalesVisitante) {
            falta.visibility = View.INVISIBLE
        }
    }

    // Actualiza la visualización del temporizador en la interfaz
    fun actualizarTemporizadorDisplay(tiempoEnMilisegundos: Long) {
        // Calcula los minutos y segundos restantes
        val minutos = (tiempoEnMilisegundos / 1000) / 60
        val segundos = (tiempoEnMilisegundos / 1000) % 60

        // Extrae los dígitos individuales para minutos y segundos
        val minutosDecenaDigit = (minutos / 10).toInt()
        val minutosUnidadDigit = (minutos % 10).toInt()
        val segundosDecenaDigit = (segundos / 10).toInt()
        val segundosUnidadDigit = (segundos % 10).toInt()

        // Actualiza las imágenes de los dígitos en la interfaz
        minutosDecena.setImageResource(getImageResourceForDigit(minutosDecenaDigit))
        minutosUnidad.setImageResource(getImageResourceForDigit(minutosUnidadDigit))
        segundosDecena.setImageResource(getImageResourceForDigit(segundosDecenaDigit))
        segundosUnidad.setImageResource(getImageResourceForDigit(segundosUnidadDigit))
    }

    fun sumarCuartos() {
        if (contadorCuartos) {
            val imagenCuartos = findViewById<ImageView>(R.id.imagenCuartos)
            numeroCuarto++
            if (numeroCuarto > 4) {
                numeroCuarto = 1
                puntuacionLocal=0
                puntuacionVisitante=0
                decenaLocal.setImageResource(getImageResourceForDigit(0))
                unidadLocal.setImageResource(getImageResourceForDigit(0))
                decenaVisitante.setImageResource(getImageResourceForDigit(0))
                unidadVisitante.setImageResource(getImageResourceForDigit(0))
            } else {
                numeroCuarto
            }
            imagenCuartos.setImageResource(getImageResourceForDigit(numeroCuarto))
        }
    }
}
