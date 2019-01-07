package com.pauenrech.regalonavidadpauenrech

import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.transition.Scene
import android.support.transition.Transition
import android.support.transition.TransitionInflater
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    var loadingScene : Scene? = null
    var homeScene: Scene? = null
    var transitionOnceLoaded: Transition? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        rootHome.background = getDrawable(R.color.colorLoadingBackground)

        /*
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        homeScene = Scene.getSceneForLayout(rootHome,R.layout.activity_home,this)
        loadingScene = Scene.getSceneForLayout(rootHome,R.layout.loading_home,this)

        loadingScene?.enter()

        Thread(Runnable {
                try {
                    Thread.sleep(2000)
                }catch (e: InterruptedException){
                    e.printStackTrace()
                }
            runOnUiThread {
                rootHome.background = getDrawable(R.drawable.gradient_background)
                val transition = TransitionInflater.from(this).inflateTransition(R.transition.no_transition)
                transition.duration = 0
                TransitionManager.go(homeScene!!,transition)
                setSupportActionBar(home_toolbar)
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


}
