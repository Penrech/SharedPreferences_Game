package com.pauenrech.regalonavidadpauenrech.model

class PreguntasData(var listaPreguntasTotal: PreguntasTotal = PreguntasTotal(),
                    var savingInterface: SaveAndGetListaPreguntas? = null)

{

    interface SaveAndGetListaPreguntas{
        fun savePreguntas(preguntas: PreguntasTotal)
        fun getPreguntas()
    }

    fun getLocalPreguntasData(){
        savingInterface?.getPreguntas()
    }

    fun addUpdatePreguntas(preguntas: PreguntasTotal){
        listaPreguntasTotal = preguntas
        savingInterface?.savePreguntas(this.listaPreguntasTotal)
    }



}