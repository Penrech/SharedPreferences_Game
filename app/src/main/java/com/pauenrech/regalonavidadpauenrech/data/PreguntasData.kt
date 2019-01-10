package com.pauenrech.regalonavidadpauenrech.data

class PreguntasData(var listaPreguntasTotal: PreguntasTotal,
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