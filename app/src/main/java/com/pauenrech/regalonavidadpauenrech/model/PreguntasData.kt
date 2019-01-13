package com.pauenrech.regalonavidadpauenrech.model


/**
 *
 *  Esta clase gestiona la obtención y guardado de la información de las preguntas del juego
 *  Tiene los siguientes parámetros:
 *  @property listaPreguntasTotal es de la clase
 *  @see PreguntasTotal , es donde se guarda toda la lista de preguntas
 *  @property savingInterface es una referencia a la interfaz
 *  @see SaveAndGetListaPreguntas
 *
 */
class PreguntasData(var listaPreguntasTotal: PreguntasTotal = PreguntasTotal(),
                    var savingInterface: SaveAndGetListaPreguntas? = null)

{

    /**
     *
     * Esta interfaz define los métodos para recibir y guardar datos de preguntas
     * Incluye los metodos:
     *
     * @see savePreguntas el cual recibe el parámetro
     * @property peguntas de la clase
     * @see PreguntasTotal
     *
     * @see getPreguntas
     *
     * Todos ellos descritos en cada uno de sus implementaciones, todas ellas en la
     * HomeActivity
     *
     * */
    interface SaveAndGetListaPreguntas{
        fun savePreguntas(preguntas: PreguntasTotal)
        fun getPreguntas()
    }

    /**
     *
     * @see getLocalPreguntasData utiliza el método de la interfaz
     * @see SaveAndGetListaPreguntas.getPreguntas para obtener datos del usuario
     *
     * */
    fun getLocalPreguntasData(){
        savingInterface?.getPreguntas()
    }

    /**
     *
     * @see addUpdatePreguntas tiene como parámetro
     * @property preguntas que es de la clase
     * @see PreguntasTotal , representa una lista de temas
     *
     * Esta funcion añade y actualiza preguntas en
     * @see listaPreguntasTotal y llama a la interfaz referenciada en
     * @see savingInterface para acceder al método
     * @see SaveAndGetListaPreguntas.savePreguntas enviando como parámetro
     * @see listaPreguntasTotal de la clase
     * @see PreguntasTotal
     *
     * */
    fun addUpdatePreguntas(preguntas: PreguntasTotal){
        listaPreguntasTotal = preguntas
        savingInterface?.savePreguntas(this.listaPreguntasTotal)
    }



}