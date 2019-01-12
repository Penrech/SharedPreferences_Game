package com.pauenrech.regalonavidadpauenrech.model

class User(
    var uid: String? = null,
    var nickname: String = "Username",
    var dificultad: Int = 0,
    var puntuacion: Int = 0,
    var ranking: Int = -1,
    var temas: List<MutableList<ThemeScore>> = listOf(mutableListOf(), mutableListOf(),
        mutableListOf()) )
{

    private var listaTemas: MutableMap<String,String> = HashMap()
    var numeroDeTemas : Int = 0

    init {
        temas[DificultadEnum.FACIL.value].forEach {
            listaTemas.put(it.id,it.name)
        }
        numeroDeTemas = listaTemas.size

    }

    enum class DificultadEnum (val value: Int) {
        FACIL(0),
        MEDIO(1),
        DIFICIL(2)
    }

    fun deleteTema(_webTemas: TemasList){
        var willBeDeleted = mutableListOf<String>()

        listaTemas.forEach { temalocal ->
            var filter = _webTemas.temas.filter { it.id == temalocal.key }
            if (filter.size == 0)
                willBeDeleted.add(temalocal.key)
        }
        temas.forEach {dificultad ->
            var indexToDelete = mutableListOf<ThemeScore>()
            dificultad.forEachIndexed { index, themeScore ->
                if (willBeDeleted.contains(themeScore.id)){
                   indexToDelete.add(themeScore)
                }
            }
            dificultad.removeAll(indexToDelete)
            indexToDelete.forEach {
                listaTemas.remove(it.id)
            }
            numeroDeTemas = listaTemas.size
        }
    }

    fun modifyTemaScore(id: String, score: Int){
        temas[dificultad].filter { it.id == id }[0].score = score
    }

    fun getTemaScore(id: String): Int{
        return temas[dificultad].filter { it.id == id }[0].score
    }

    fun addTemaLista(name: String, id: String){

        if (!listaTemas.contains(id)) {
            listaTemas.put(id,name)
            temas.forEachIndexed { index, it ->
                it.add(ThemeScore(name, id, index,0))
            }
            numeroDeTemas = listaTemas.size
        }
        else{
            if(listaTemas[id] != name){
                listaTemas[id] = name
                temas.forEach { mutableList ->
                    mutableList.forEach {
                        if (it.id == id){
                            it.name = name
                        }
                    }
                }
            }
        }
    }

    class ThemeScore(var name: String,
                     var id: String,
                     var dificultadid: Int,
                     var score: Int)

}