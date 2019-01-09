package com.pauenrech.regalonavidadpauenrech.data

import com.pauenrech.regalonavidadpauenrech.HomeActivity


class UserData(var user: User = User(),
               var savingInterface: SaveAndGetLocalUserData? = null)
{

    interface SaveAndGetLocalUserData{
        fun saveUserData(user: User)
        fun getUserData()
    }

    private var listaTemas: MutableList<String> = mutableListOf()

    init {
        user.temas.facil.forEach {
            listaTemas.add(it.id)
        }
    }

    fun getLocalUserData(){
        savingInterface?.getUserData()
    }

    fun changeNickname(nickname: String){
        this.user.nickname = nickname
        savingInterface?.saveUserData(this.user)
    }

    fun changeDificultad(dificultad: Int){
        this.user.dificultad = dificultad
        savingInterface?.saveUserData(this.user)
    }

    fun changePuntuacion(puntuacion: Int){
        this.user.puntuacion = puntuacion
        savingInterface?.saveUserData(this.user)
    }

    fun changeRanking(ranking: Int){
        this.user.ranking = ranking
        savingInterface?.saveUserData(this.user)
    }

    fun ActualizarTemas(_temas: MutableList<Tema>){
        var listaTemasAEliminar: MutableList<String> = mutableListOf()
            _temas.forEach {
                user.temas.addTema(it.name, it.id)
            }
        savingInterface?.saveUserData(this.user)
    }



}