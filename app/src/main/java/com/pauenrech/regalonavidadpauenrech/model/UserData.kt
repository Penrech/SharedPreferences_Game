package com.pauenrech.regalonavidadpauenrech.model

/**
 *
 *  Esta clase gestiona la obtención y guardado de la información de usuario
 *  Tiene los siguientes parámetros:
 *  @property user es de la clase
 *  @see User , es donde se guardan propiamente los datos del usuario
 *  @property savingInterface es una referencia a la interfaz
 *  @see SaveAndGetLocalUserData
 *
 */
class UserData(
    var user: User = User(),
    var savingInterface: SaveAndGetLocalUserData? = null)
{

    /**
     *
     * Esta interfaz define los métodos para recibir y guardar datos de usuario
     * Incluye los metodos:
     *
     * @see saveUserData el cual recibe el parámetro
     * @property user de la clase
     * @see User
     *
     * @see updateMainPuntuacionTextView el cual recibe el parámetro
     * @property puntuacion , que es un primitiva
     * @see Int
     *
     * @see getUserData
     *
     * Todos ellos descritos en cada uno de sus implementaciones, todas ellas en la
     * HomeActivity
     *
     * */
    interface SaveAndGetLocalUserData{
        fun saveUserData(user: User)
        fun updateMainPuntuacionTextView(puntuacion: Int)
        fun getUserData()
    }

    /**
     *
     * @see getLocalUserData utiliza el método de la interfaz
     * @see SaveAndGetLocalUserData.getUserData para obtener datos del usuario
     *
     * */
    fun getLocalUserData(){
        savingInterface?.getUserData()
    }

    /**
     *
     * @see setUid tiene como parámetro
     * @property uid y sirve para introducir una uid al usuario a través de su variable
     * @see user de la clase
     * @see User y la interfaz
     * @see SaveAndGetLocalUserData.saveUserData con la clase
     * @see User como parámetro
     *
     * */
    fun setUid(uid: String){
        this.user.uid = uid
        savingInterface?.saveUserData(this.user)
    }

    /**
     *
     * @see changeNickname tiene como parámetro
     * @property nickname y sirve para modificar el nickname del usuario a través de su variable
     * @see user de la clase
     * @see User y la interfaz
     * @see SaveAndGetLocalUserData.saveUserData con la clase
     * @see User como parámetro
     *
     * */
    fun changeNickname(nickname: String){
        this.user.nickname = nickname
        this.user.nicknameLowerCase = nickname.toLowerCase()
        savingInterface?.saveUserData(this.user)
    }

    /**
     *
     * @see changeDificultad tiene como parámetro
     * @property dificultad y sirve para modificar el nickname del usuario a través de su variable
     * @see user de la clase
     * @see User y la interfaz
     * @see SaveAndGetLocalUserData.saveUserData con la clase
     * @see User como parámetro
     *
     * */
    fun changeDificultad(dificultad: Int){
        this.user.dificultad = dificultad
        savingInterface?.saveUserData(this.user)
    }

    /**
     *
     * @see changePuntuacion tiene como parámetro
     * @property puntuacion y sirve para modificar el nickname del usuario a través de su variable
     * @see user de la clase
     * @see User y la interfaz
     * @see SaveAndGetLocalUserData.saveUserData con la clase
     * @see User como parámetro
     *
     * */
    fun changePuntuacion(puntuacion: Int){
        this.user.puntuacion = puntuacion
        savingInterface?.saveUserData(this.user)
        savingInterface?.updateMainPuntuacionTextView(puntuacion)
    }

    /**
     *
     * @see changeRanking tiene como parámetro
     * @property ranking y sirve para modificar el nickname del usuario a través de su variable
     * @see user de la clase
     * @see User y la interfaz
     * @see SaveAndGetLocalUserData.saveUserData con la clase
     * @see User como parámetro
     *
     * */
    fun changeRanking(ranking: Int){
        this.user.ranking = ranking
        savingInterface?.saveUserData(this.user)
    }

    /**
     *
     * @see actualizarPuntuacionTema recibe como parámetro
     * @property idTema , un string que define la id de un determinado tema y
     * @property score , un int que define la nueva puntuación de un determinado tema
     *
     * Este método sirve para modficiar la puntuación de un tema de un usuario a través de la variable
     * @see user y el método de su clase
     * @see User.modifyTemaScore al cual se le pasán las dos propiedades de
     * @see actualizarPuntuacionTema
     *
     * Finalmente se guarda la información de la variable
     * @see user de clase
     * @see User a través de la interfaz
     * @see SaveAndGetLocalUserData.saveUserData
     *
     * */
    fun actualizarPuntuacionTema(idTema: String,score: Int){
        user.modifyTemaScore(idTema,score)
        savingInterface?.saveUserData(this.user)

    }

    /**
     *
     * @see ActualizarTemas recibe como parámetro
     * @property _temas que es de la clase
     * @see TemasList , que representa una lista de temas que se utilizarán para actualizar los temas del usuario
     *
     * Primero se llama a la funcion de la variable
     * @see user que esta dentro de su clase
     * @see User.deleteTema pasandole la propiedad anterior.
     *
     * Seguidamente se recorre toda la lista de temas que están dentro de la propiedad
     * @property _temas incluida en su clase
     * @see User.temas
     * Por cada tema se llama al método
     * @see User.addTemaLista pasandole como parámetro el nombre y la id del tema en cuestión
     *
     * Finalmente se guarda la información de la variable
     * @see user de clase
     * @see User a través de la interfaz
     * @see SaveAndGetLocalUserData.saveUserData
     *
     * */
    fun ActualizarTemas(_temas: TemasList){
         user.deleteTema(_temas)
        _temas.temas.forEach {
            user.addTemaLista(it.name,it.id)
        }
        savingInterface?.saveUserData(this.user)
    }



}