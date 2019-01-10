package com.pauenrech.regalonavidadpauenrech.data

class PreguntasTema(var id: String,
                    var name: String,
                    var preguntas: MutableList<Pregunta>)
{

    fun getRandomQuestions(): List<Pregunta>{
        val randomQuestions = preguntas.toMutableList()
        randomQuestions.shuffle()
        return randomQuestions.slice(0 until 10)
    }

}