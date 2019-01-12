package com.pauenrech.regalonavidadpauenrech.model

import android.util.Log
import java.util.*


class Pregunta(var id: String? = null,
               var respuesta_correcta: String? = null,
               var respuesta_incorrecta: String? = null,
               var puntuacion: Int? = null)
{
    private var topAnswer : Pair<String,Boolean>? = null
    private var bottomAnswer: Pair<String,Boolean>? = null

    fun getRandomAnswer(): Pair<String,Boolean>{
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
        Log.i("ANSWER","random : $random, Top answer: ${topAnswer?.first}")
        return topAnswer!!
    }

    fun checkAnswer(answer: Boolean): Boolean{
        return topAnswer?.second == answer
    }
}