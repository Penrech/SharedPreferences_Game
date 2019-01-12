package com.pauenrech.regalonavidadpauenrech.model



class UserData(var user: User = User(),
               var savingInterface: SaveAndGetLocalUserData? = null)
{

    interface SaveAndGetLocalUserData{
        fun saveUserData(user: User)
        fun updateMainPuntuacionTextView(puntuacion: Int)
        fun getUserData()
    }


    fun getLocalUserData(){
        savingInterface?.getUserData()
    }

    fun setUid(uid: String){
        this.user.uid = uid
        savingInterface?.saveUserData(this.user)
    }

    fun changeNickname(nickname: String){
        this.user.nickname = nickname
        this.user.nicknameLowerCase = nickname.toLowerCase()
        savingInterface?.saveUserData(this.user)
    }

    fun changeDificultad(dificultad: Int){
        this.user.dificultad = dificultad
        savingInterface?.saveUserData(this.user)
    }

    fun changePuntuacion(puntuacion: Int){
        this.user.puntuacion = puntuacion
        savingInterface?.saveUserData(this.user)
        savingInterface?.updateMainPuntuacionTextView(puntuacion)
    }

    fun changeThemeScore(newScore: Int, dificultad: Int, temaId: String){
        val ref = user.temas[dificultad].filter { it.id == temaId }
        ref[0].score = newScore
        savingInterface?.saveUserData(this.user)
    }

    fun changeRanking(ranking: Int){
        this.user.ranking = ranking
        savingInterface?.saveUserData(this.user)
    }

    fun actualizarPuntuacionTema(idTema: String,score: Int){
        user.modifyTemaScore(idTema,score)
        savingInterface?.saveUserData(this.user)

    }

    fun ActualizarTemas(_temas: TemasList){
         user.deleteTema(_temas)
        _temas.temas.forEach {
            user.addTemaLista(it.name,it.id)
        }
        savingInterface?.saveUserData(this.user)
    }



}