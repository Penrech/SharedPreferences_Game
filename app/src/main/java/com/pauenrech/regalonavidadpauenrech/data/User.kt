package com.pauenrech.regalonavidadpauenrech.data

class User(
    var nickname: String = "Username",
    var dificultad: Int = 0,
    var puntuacion: Int = 0,
    var ranking: Int = -1,
    var temas: Temas = Temas())
{


    class Temas(
        var facil: MutableList<ThemeScore> = mutableListOf(),
        var medio : MutableList<ThemeScore> = mutableListOf(),
        var dificil: MutableList<ThemeScore> = mutableListOf()){

        private var listaTemas: MutableMap<String,String> = HashMap()
        enum class dificultadEnum(dificultad: Int){
            FACIL(0),
            MEDIO(1),
            DIFICIL(2)
        }

        var megalista: List<MutableList<ThemeScore>> = mutableListOf(mutableListOf(), mutableListOf(),
            mutableListOf())

        fun addTemaLista(name: String, id: String){
            val newTheme = ThemeScore(name,id,0)
            if (!listaTemas.contains(id)) {
                listaTemas.put(id,name)
                megalista.forEach {
                    it.add(newTheme)
                }
            }
            else{
                if(listaTemas[id] != name){
                    listaTemas[id] = name
                    megalista.forEach { mutableList ->
                        mutableList.forEach {
                            if (it.id == id){
                                it.name = name
                            }
                        }
                    }
                }
            }
        }


        fun addTema(name: String, id: String){
            val newTheme = ThemeScore(name,id,0)
            if (!listaTemas.contains(id)) {
                listaTemas.put(id,name)
                facil.add(newTheme)
                medio.add(newTheme)
                dificil.add(newTheme)
            }
            else{
                if(listaTemas[id] != name){
                    listaTemas[id] = name
                        val listOfElementsToChange: MutableList<List<ThemeScore>> = mutableListOf()
                        listOfElementsToChange.add(facil.filter { it.id == id })
                        listOfElementsToChange.add(medio.filter { it.id == id })
                        listOfElementsToChange.add(dificil.filter { it.id == id })
                        listOfElementsToChange.forEach {
                            it[0].name = name
                        }
                }
            }
        }
    }

    class ThemeScore(var name: String,
                     var id: String,
                     var score: Int)

}