package com.pauenrech.regalonavidadpauenrech.model

class PreguntasTema(var id: String?= null,
                    var name: String?= null,
                    var preguntas: MutableList<Pregunta> = mutableListOf())
{

    fun getRandomQuestions(): List<Pregunta>{
        val randomQuestions = preguntas.toMutableList()
        randomQuestions.shuffle()
        return randomQuestions.slice(0 until 10)
    }

}