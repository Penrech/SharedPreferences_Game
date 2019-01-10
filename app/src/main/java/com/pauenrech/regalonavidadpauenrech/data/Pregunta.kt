package com.pauenrech.regalonavidadpauenrech.data

import java.util.*


class Pregunta(var id: String,
               var respuesta_correcta: String,
               var respuesta_incorrecta: String,
               var puntuacion: Int)
{
    private var topAnswer : Pair<String,Boolean>? = null
    private var bottomAnswer: Pair<String,Boolean>? = null

    fun getRandomAnswer(): Pair<String,String>{
        val random = Random().nextInt(3)
        when(random){
            1 -> {
                topAnswer = Pair(respuesta_correcta,true)
                bottomAnswer = Pair(respuesta_incorrecta,false)
            }
            2 ->{
                topAnswer = Pair(respuesta_incorrecta,false)
                bottomAnswer = Pair(respuesta_correcta,true)
            }
        }
        return Pair(topAnswer!!.first,bottomAnswer!!.first)
    }

    fun checkAnswer(answer: Boolean): Boolean{
        return topAnswer?.second == answer
    }
}