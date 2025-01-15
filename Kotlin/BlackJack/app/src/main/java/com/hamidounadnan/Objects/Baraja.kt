package com.hamidounadnan.Objects

import com.hamidounadnan.blackjack.R

class Baraja {
    // Lista para almacenar las cartas del mazo
    val cartas: MutableList<Carta> = mutableListOf()

    init {
        crearCartas()
    }


    private fun crearCartas() {
        //Creo una lista de cartas y a su vez creo cartas dentro de la lista
        val cartasData = listOf(
            "As" to 11, "Dos" to 2, "Tres" to 3, "Cuatro" to 4, "Cinco" to 5,
            "Seis" to 6, "Siete" to 7, "Ocho" to 8, "Nueve" to 9, "Diez" to 10,
            "J" to 10, "Q" to 10, "K" to 10
        )

        //Creo palos para diferenciar los drawables a la hora de asignarlos
        val palos = listOf("Diamantes", "Corazones", "Tréboles", "Picas")


        for (palo in palos) {
            for ((nombre, valor) in cartasData) {
                val nombreCarta = "$nombre de $palo"
                val imagenResId = obtenerImagenResId(nombre, palo)
                cartas.add(Carta(nombreCarta, valor, imagenResId))
            }
        }
    }

    // Método auxiliar para asignar el recurso de imagen correspondiente
    private fun obtenerImagenResId(nombre: String, palo: String): Int {
        return when ("$nombre de $palo") {
            "As de Diamantes" -> R.drawable.asdiamante
            "Dos de Diamantes" -> R.drawable.dosdiamante
            "Tres de Diamantes" -> R.drawable.tresdiamante
            "Cuatro de Diamantes" -> R.drawable.cuatrodiamante
            "Cinco de Diamantes" -> R.drawable.cincodiamante
            "Seis de Diamantes" -> R.drawable.seisdiamante
            "Siete de Diamantes" -> R.drawable.sietediamante
            "Ocho de Diamantes" -> R.drawable.ochodiamante
            "Nueve de Diamantes" -> R.drawable.nuevediamante
            "Diez de Diamantes" -> R.drawable.diezdiamante
            "J de Diamantes" -> R.drawable.jtoadiamante
            "Q de Diamantes" -> R.drawable.qdiamante
            "K de Diamantes" -> R.drawable.kdiamante

            "As de Corazones" -> R.drawable.ascorazon
            "Dos de Corazones" -> R.drawable.doscorazon
            "Tres de Corazones" -> R.drawable.trescorazon
            "Cuatro de Corazones" -> R.drawable.cuatrocorazon
            "Cinco de Corazones" -> R.drawable.cincocorazon
            "Seis de Corazones" -> R.drawable.seiscorazon
            "Siete de Corazones" -> R.drawable.sietecorazon
            "Ocho de Corazones" -> R.drawable.ochocorazon
            "Nueve de Corazones" -> R.drawable.nuevecorazon
            "Diez de Corazones" -> R.drawable.diezcorazon
            "J de Corazones" -> R.drawable.jotacorazon
            "Q de Corazones" -> R.drawable.qcorazon
            "K de Corazones" -> R.drawable.kcorazon

            "As de Tréboles" -> R.drawable.astrebol
            "Dos de Tréboles" -> R.drawable.dostrebol
            "Tres de Tréboles" -> R.drawable.trestrebol
            "Cuatro de Tréboles" -> R.drawable.cuatrotrebol
            "Cinco de Tréboles" -> R.drawable.cincotrebol
            "Seis de Tréboles" -> R.drawable.seistrebol
            "Siete de Tréboles" -> R.drawable.sietetrebol
            "Ocho de Tréboles" -> R.drawable.ochotrebol
            "Nueve de Tréboles" -> R.drawable.nuevetrebol
            "Diez de Tréboles" -> R.drawable.dieztrebol
            "J de Tréboles" -> R.drawable.jotatrebol
            "Q de Tréboles" -> R.drawable.qtrebol
            "K de Tréboles" -> R.drawable.ktrebol

            "As de Picas" -> R.drawable.aspicas
            "Dos de Picas" -> R.drawable.dospicas
            "Tres de Picas" -> R.drawable.trespicas
            "Cuatro de Picas" -> R.drawable.cuatropicas
            "Cinco de Picas" -> R.drawable.cincopicas
            "Seis de Picas" -> R.drawable.seispicas
            "Siete de Picas" -> R.drawable.sietepicas
            "Ocho de Picas" -> R.drawable.ochopicas
            "Nueve de Picas" -> R.drawable.nuevepicas
            "Diez de Picas" -> R.drawable.diezpicas
            "J de Picas" -> R.drawable.jotapicas
            "Q de Picas" -> R.drawable.qpicas
            "K de Picas" -> R.drawable.kpicas

            else -> R.drawable.ic_launcher_background

        }
    }

        fun obtenerCartaAleatoria(): Carta {
            val indiceAleatorio = cartas.indices.random()
            return cartas[indiceAleatorio]
        }

}
