package com.pauenrech.regalonavidadpauenrech.model

/**
 *
 *  Esta clase gestiona la obtención y guardado de la información de temas
 *  Tiene los siguientes parámetros:
 *  @property lista es de la clase
 *  @see TemasList, es donde se guarda la lista de temas
 *  @property savingInterface es una referencia a la interfaz
 *  @see SaveOrGetListaTemas
 *
 */
class TemaData(
    var lista: TemasList = TemasList() ,
    var savingInterface: SaveOrGetListaTemas? = null)
{

    /**
     *
     * Esta interfaz define los métodos para recibir y guardar datos de temas
     * Incluye los metodos:
     *
     * @see saveListaTemas el cual recibe el parámetro
     * @property temas de la clase
     * @see TemasList
     *
     * @see getListaTemas
     *
     * Todos ellos descritos en cada uno de sus implementaciones, todas ellas en la
     * HomeActivity
     *
     * */
    interface SaveOrGetListaTemas{
        fun saveListaTemas(temas: TemasList)
        fun getListaTemas()
    }

    /**
     *
     * @see addUpdateTemas tiene como parámetro
     * @property temas que es de la clase
     * @see TemasList , representa una lista de temas
     *
     * Esta funcion añade y actualiza temas en
     * @see lista y llama a la interfaz referenciada en
     * @see savingInterface para acceder al método
     * @see SaveOrGetListaTemas.saveListaTemas enviando como parámetro
     * @see lista de la clase
     * @see TemasList
     *
     * */
    fun addUpdateTemas(temas: TemasList){
        this.lista = temas
        savingInterface?.saveListaTemas(this.lista)
    }

    /**
     *
     * @see getLocalListaTemas utiliza el método de la interfaz
     * @see SaveOrGetListaTemas.getListaTemas para obtener datos del usuario
     *
     * */
    fun getLocalListaTemas(){
        savingInterface?.getListaTemas()
    }
}