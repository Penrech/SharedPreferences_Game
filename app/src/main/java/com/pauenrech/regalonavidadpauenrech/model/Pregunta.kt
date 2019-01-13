package com.pauenrech.regalonavidadpauenrech.model

import java.util.*

/**
 *
 * Esta clase implementa el modelo de básico de pregunta, donde se guardan los datos de una pregunta en concreto.
 * Todas su propiedades están inicializadas de alguna manera para crear un constructor sin parámetros de entrada
 * obligatorios. Esto es necesario para rellenar esta clase con datos de Firebase en caso de que fuera necesario
 *
 * Cuenta con las siguientes propiedades:
 * @property id es la id de la pregunta
 * @property respuesta_correcta es la respuesta correcta de la pregunta
 * @property respuesta_incorrecta es una respuesta alternativa que es erronea
 * @property puntuacion es el valor que tiene dicha pregunta
 *
 */
class Pregunta(
    var id: String? = null,
    var respuesta_correcta: String? = null,
    var respuesta_incorrecta: String? = null,
    var puntuacion: Int? = null)
{
    /**
     *
     * @see topAnswer es un
     * @see Pair de String y un Boolean que representa la pregunta que se le mostrará al usuario
     *
     * @see bottomAnswer es un
     * @see Pair de String y un Boolean que repesenta cual es la pregunta oculta al usuario. Esto se implementa de
     * esta manera ya que inicialmente se podría, tras finalizar el juego, se podría voltear las preguntas respuestas
     * de forma incorrecta para ver su respuesta correcta. Esto no ha sido implementado
     *
     *
     * */
    private var topAnswer : Pair<String,Boolean>? = null
    private var bottomAnswer: Pair<String,Boolean>? = null

    /**
     *
     * @see getRandomAnswer es un método que escoge aleatoriamente entre
     * @see respuesta_correcta y
     * @see respuesta_incorrecta y se la muestra al usuario
     *
     * @return un
     * @see Pair de un String y un Boolean, siendo el string la respuesta y el boolean si es correcta o no
     *
     * */
    fun getRandomAnswer(): Pair<String,Boolean>{
        /**
         *
         * Para escoger la respuesta aleatoria se crea la variable
         * @param random que utiliza el método
         * @see Random.nextInt con el parámetro [2] para obtener un número aleatorio del 0 al 1
         *
         * Seguidamente se utiliza un selector when con
         * @param random como parámetro
         *
         * Si
         * @param random es [0]
         * La respuesta
         * @see topAnswer , la que se muestra al usuario, es la
         * @see respuesta_correcta
         * La respuesta
         * @see bottomAnswer , la que no se muestra, es la
         * @see respuesta_incorrecta
         *
         * si
         * @param random es [1]
         * La respuesta
         * @see topAnswer , la que se muestra al usuario, es la
         * @see respuesta_incorrecta
         * La respuesta
         * @see bottomAnswer , la que no se muestra, es la
         * @see respuesta_correcta
         *
         * Finalmente se devuelve como resultado
         * @return
         * @see topAnswer
         *
         * */
        val random = Random().nextInt(2)
        when(random){
            0 -> {
                topAnswer = Pair(respuesta_correcta!!,true)
                bottomAnswer = Pair(respuesta_incorrecta!!,false)
            }
            1 ->{
                topAnswer = Pair(respuesta_incorrecta!!,false)
                bottomAnswer = Pair(respuesta_correcta!!,true)
            }
        }
        return topAnswer!!
    }
}