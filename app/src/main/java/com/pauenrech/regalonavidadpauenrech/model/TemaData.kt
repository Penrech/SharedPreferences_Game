package com.pauenrech.regalonavidadpauenrech.model

class TemaData(var lista: TemasList = TemasList() ,
               var savingInterface: SaveOrGetListaTemas? = null
)
{
    interface SaveOrGetListaTemas{
        fun saveListaTemas(temas: TemasList)
        fun getListaTemas()
    }

    fun addUpdateTemas(temas: TemasList){
        this.lista = temas
        savingInterface?.saveListaTemas(this.lista)
    }

    fun getLocalListaTemas(){
        savingInterface?.getListaTemas()
    }

}