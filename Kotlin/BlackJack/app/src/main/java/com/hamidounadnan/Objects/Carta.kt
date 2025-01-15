package com.hamidounadnan.Objects

data class Carta(
    private var tipoCarta: String,
    private var valor: Int,
    private var imagenResId: Int



){
    fun getTipoCarta(): String {
        return tipoCarta
    }

    fun setTipoCarta(tipoCarta: String) {
        this.tipoCarta = tipoCarta
    }


    fun getValor(): Int {
        return valor
    }

    fun setValor(valor: Int) {
        this.valor = valor
    }


    fun getImagenResId(): Int {
        return imagenResId
    }

    fun setImagenResId(imagenResId: Int) {
        this.imagenResId = imagenResId
    }
}

