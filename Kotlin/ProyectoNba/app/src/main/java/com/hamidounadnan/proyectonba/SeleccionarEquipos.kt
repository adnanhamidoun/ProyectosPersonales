package com.hamidounadnan.proyectonba

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.snackbar.Snackbar

class SeleccionarEquipos : AppCompatActivity() {
    var esEquipoLocal = true
    var equipoLocalId: Int? = null
    var equipoVisitanteId: Int? = null
    var equipoLocalDrawable: Drawable? = null
    var equipoVisitanteDrawable: Drawable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.seleccion_equipos)
        var equipo1 = findViewById<ImageView>(R.id.equipo1)
        var equipo2 = findViewById<ImageView>(R.id.equipo2)
        equipo1.visibility = View.INVISIBLE
        equipo2.visibility = View.INVISIBLE



    }

    fun seleccionarEquipo(view: View) {
        var equipo1 = findViewById<ImageView>(R.id.equipo1)
        var equipo2 = findViewById<ImageView>(R.id.equipo2)
        val botonSeleccionado = view as ImageButton
        val drawableBoton = botonSeleccionado.drawable

        // Obtengo el ID del drawable asociado al botón
        val equipoDrawableId = when (botonSeleccionado.id) {
            R.id.lakersButton -> R.drawable.lakers
            R.id.celticsButton -> R.drawable.celtics
            R.id.cavaliersButton -> R.drawable.cavaliers
            R.id.warriorsButton -> R.drawable.warriors
            R.id.hawksButton -> R.drawable.hawks
            R.id.timberwolvesButton -> R.drawable.timberwolves
            R.id.raptorsButton -> R.drawable.raptors
            R.id.sacramentokingsButton -> R.drawable.sacramentokings
            R.id.chicagobullsButton -> R.drawable.chicagobulls
            R.id.ers76Button -> R.drawable.ers76
            R.id.knicksButton -> R.drawable.knicks
            R.id.sonicsButton -> R.drawable.sonics
            else -> null
        }


        if (equipoDrawableId == null) {
            mostrarSnackbar(view, "Equipo no encontrado")
            return
        }

        // Verificamos si el equipo es local o visitante
        if (esEquipoLocal) {
            // Verificar si ya ha sido seleccionado como visitante
            if (equipoVisitanteDrawable == drawableBoton) {
                mostrarSnackbar(view, "Este equipo ya ha sido seleccionado como visitante")
                return
            }
            equipoLocalId = equipoDrawableId // Asigna el ID del equipo local

            equipoLocalDrawable = drawableBoton // Asigna el drawable local
            findViewById<ImageView>(R.id.equipo1).setImageDrawable(drawableBoton) // Muestra el equipo local
            equipo1.visibility = View.VISIBLE
        } else {
            // Verificar si ya ha sido seleccionado como local
            if (equipoLocalDrawable == drawableBoton) {
                mostrarSnackbar(view, "Este equipo ya ha sido seleccionado como local")
                return
            }
            equipoVisitanteId = equipoDrawableId // Asigna el ID del equipo visitante
            equipoVisitanteDrawable = drawableBoton // Asigna el drawable visitante
            findViewById<ImageView>(R.id.equipo2).setImageDrawable(drawableBoton) // Muestra el equipo visitante
            equipo2.visibility = View.VISIBLE
        }
    }
    fun empezarPartido(view: View) {
        // Verificar que ambos equipos han sido seleccionados
        if (equipoLocalId == null || equipoVisitanteId == null) {
            mostrarSnackbar(view, "Por favor selecciona ambos equipos")
            return
        }
        val intent = Intent(this, CargandoPartido::class.java)
        intent.putExtra("equipoLocal", equipoLocalId)
        intent.putExtra("equipoVisitante", equipoVisitanteId)
        startActivity(intent)
    }
     fun mostrarSnackbar(view: View, mensaje: String) {
        val snackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(resources.getColor(R.color.black))
        snackbar.setTextColor(resources.getColor(R.color.white))
        snackbar.show()
    }


    fun cambiarEquipo(view: View) {

        val botonCambiarEquipo = findViewById<Button>(R.id.cambiarVisitante_Local_Button)
        val textEligeEquipo = findViewById<TextView>(R.id.textElige_Equipo)


        esEquipoLocal = !esEquipoLocal


        textEligeEquipo.text = if (esEquipoLocal) {
            "Elige el equipo número 1 (LOCAL)"
        } else {
            "Elige el equipo número 2 (VISITANTE)"
        }


        botonCambiarEquipo.text = if (esEquipoLocal) {
            "Visitante"
        } else {
            "Local"
        }
    }

}