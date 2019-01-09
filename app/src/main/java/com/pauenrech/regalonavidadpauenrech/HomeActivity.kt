package com.pauenrech.regalonavidadpauenrech


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.transition.Scene
import android.support.transition.Transition
import android.support.transition.TransitionInflater
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.pauenrech.regalonavidadpauenrech.data.UserData

import kotlinx.android.synthetic.main.activity_home.*
import com.google.gson.Gson
import com.pauenrech.regalonavidadpauenrech.data.Tema
import com.pauenrech.regalonavidadpauenrech.data.User
import kotlinx.android.synthetic.main.content_home.*
import kotlin.properties.Delegates


class HomeActivity : AppCompatActivity(), UserData.SaveAndGetLocalUserData {

    var loadingScene : Scene? = null
    var homeScene: Scene? = null
    var transitionOnceLoaded: Transition? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        rootHome.background = getDrawable(android.R.color.background_light)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        gson = Gson()
        userData.savingInterface = this as UserData.SaveAndGetLocalUserData

        homeScene = Scene.getSceneForLayout(rootHome,R.layout.activity_home,this)
        loadingScene = Scene.getSceneForLayout(rootHome,R.layout.loading_home,this)

        loadingScene?.enter()

        Thread(Runnable {
                try {
                    Thread.sleep(2000)
                    userData.getLocalUserData()
                }catch (e: InterruptedException){
                    e.printStackTrace()
                }
            runOnUiThread {
                rootHome.background = getDrawable(R.drawable.gradient_background)
                val transition = TransitionInflater.from(this).inflateTransition(R.transition.no_transition)
                transition.duration = 0
                TransitionManager.go(homeScene!!,transition)
                setSupportActionBar(home_toolbar)
                homeHighScore.text = "${userData.user.puntuacion}"
                /**
                 *
                 * Evito que se vea el titulo del activity
                 *
                 * */
                supportActionBar?.setDisplayShowTitleEnabled(false)

            }
        }).start()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_profile -> {
                showProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

/*
    fun addTemas(){
        var temas: MutableList<Tema> = mutableListOf()
        temas.add(Tema("Geografía","geografia","#80CBC4","#00BFA5"))
        temas.add(Tema("Tecnología","tecnologia","#81D4FA","#0091EA"))
        temas.add(Tema("Animales","animales","#FFCC80","#FF6D00"))
        userData.ActualizarTemas(temas)
        retriveTemas()
    }

    fun actualizarTemas(view: View){
        var temas: MutableList<Tema> = mutableListOf()
        temas.add(Tema("Geografía","geografia","#80CBC4","#00BFA5"))
        temas.add(Tema("Tecnología","tecnologia","#81D4FA","#0091EA"))
        temas.add(Tema("Animales","animales","#FFCC80","#FF6D00"))
        userData.ActualizarTemas(temas)
        retriveTemas()
    }

    fun retriveTemas(){
        userData.user.temas.facil.forEach {
            Log.i("TAG","Temas faciles: ${it.name}")
        }
        userData.user.temas.medio.forEach {
            Log.i("TAG","Temas medios: ${it.name}")
        }
        userData.user.temas.medio.forEach {
            Log.i("TAG","Temas dificiles: ${it.name}")
        }
    }*/

    /**
     *
     * Esta función se usa simplemente para abrir la actividad perfil a través de un intent sin animaciones
     *
     * */
    fun showProfile(){
        val intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0,0)
    }

    companion object {
        val USER_DATA = "userData"
        var userData = UserData()
        var sharedPreferences: SharedPreferences? = null
        var gson : Gson? = null
    }

    override fun saveUserData(user: User) {
        val prefsEditor = sharedPreferences?.edit()
        val json = gson?.toJson(user)
        prefsEditor?.putString(USER_DATA,json)
        prefsEditor?.apply()
    }

    override fun getUserData() {
        if (sharedPreferences!!.contains(USER_DATA)){
            val json = sharedPreferences?.getString(USER_DATA,"")
            userData.user = gson!!.fromJson(json,User::class.java)
        }
    }

}
