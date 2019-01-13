package com.pauenrech.regalonavidadpauenrech.model

/**
 *
 * Esta clase tiene como parámetro
 * @property id que es el id del tema de las preguntas
 * @property name que es el nombre del tema de las preguntas
 * @property preguntas que es una lista de
 * @see Pregunta
 *
 * Todas las propiedades de esta clase están inicializados para emular a un constructor vacio necesario para servir
 * de modelo para la BBDD de Firebase
 *
 * */
class PreguntasTema(
    var id: String?= null,
    var name: String?= null,
    var preguntas: MutableList<Pregunta> = mutableListOf())
{

    /**
     *
     * @see getRandomQuestions obtiene 10 preguntas aleatorias del conjunto de preguntas de un tema
     * @return devuelve una
     * @see List de preguntas de la clase
     * @see Pregunta
     *
     * */
    fun getRandomQuestions(): List<Pregunta>{

        /**
         *
         * Se define una variable
         * @param randomQuestions que es una copia de la lista
         * @see preguntas
         *
         * Se utiliza el método
         * @see MutableList.shuffle para mezclar aleatoriamente las preguntas de la lista
         *
         * Finalmente se utiliza el método
         * @see MutableList.slice para extraer las primeras diez preguntas de la lista aleatoria y se devuelven como
         * resultado
         *
         * */
        val randomQuestions = preguntas.toMutableList()
        randomQuestions.shuffle()
        return randomQuestions.slice(0 until 10)
    }
}