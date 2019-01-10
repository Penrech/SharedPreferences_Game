package com.pauenrech.regalonavidadpauenrech


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
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
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_home.*
import com.google.gson.Gson
import com.pauenrech.regalonavidadpauenrech.data.*
import kotlinx.android.synthetic.main.content_home.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp


class HomeActivity : AppCompatActivity(),
    UserData.SaveAndGetLocalUserData,
    TemaData.SaveOrGetListaTemas{



    var loadingScene : Scene? = null
    var homeScene: Scene? = null
    var transitionOnceLoaded: Transition? = null
    var database : FirebaseDatabase? = null
    var conectionRef: DatabaseReference? = null
    var preguntasRef: DatabaseReference? = null
    var temasRef: DatabaseReference? = null
    var usuariosRef: DatabaseReference? = null
    var dummyRef: DatabaseReference? = null
    var mainUILoaded: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        database = FirebaseDatabase.getInstance()

        conectionRef = database?.getReference(".info/connected")
        preguntasRef = database?.getReference("preguntas")!!.child("Es_es")
        temasRef = database?.getReference("temas")!!.child("ES_es")
        usuariosRef = database?.getReference("usuarios")
        dummyRef = database?.getReference("conection")

        userData.getLocalUserData()
        temasData.getLocalListaTemas()

        conectionRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    conectionState = connected

                    Log.i("TAG","Conectado")
                } else {
                    conectionState = connected
                    Log.i("TAG","No conectado")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("TAG","Error detectando conexion $error")
            }
        })
        getTemasFromFirebase()

        rootHome.background = getDrawable(android.R.color.background_light)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        gson = Gson()
        userData.savingInterface = this as UserData.SaveAndGetLocalUserData
        temasData.savingInterface = this as TemaData.SaveOrGetListaTemas


        homeScene = Scene.getSceneForLayout(rootHome,R.layout.activity_home,this)
        loadingScene = Scene.getSceneForLayout(rootHome,R.layout.loading_home,this)

        loadingScene?.enter()



        setTimer(2)

       // getTemasFromFirebase()





    }
/*
    fun comprobarConexion(): Boolean{

        conectionRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    Log.i("TAG","Conectado")
                } else {
                    Log.i("TAG","No conectado")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("TAG","Error detectando conexion $error")
            }
        })
    }*/

    fun setTimer(seconds: Int){
        val miliseconds = (seconds * 1000).toLong()
        Handler().postDelayed(
            {
                if (conectionState){
                    if (!mainUILoaded)
                    loadHome(true)
                }
                else{
                    loadHome(false)
                }
            },
            miliseconds
        )
    }


    fun getTemasFromFirebase(){

        // Read from the database
        temasRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val temasList = TemasList()
                for ( i in dataSnapshot.children){
                    temasList.temas.add(i.getValue(Tema::class.java)!!)
                }
                temasData.addUpdateTemas(temasList)
                userData.ActualizarTemas(temasList)
                retriveTemas()
                if (!mainUILoaded)
                    loadHome(true)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,"Error de conexión",Toast.LENGTH_LONG).show()
                // Failed to read value
                Log.i("TAG", "Failed to read value.", error.toException())
            }
        })

    }

    fun getPreguntasFromFirebase(){
        // Read from the database
        preguntasRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(PreguntasTotal::class.java)

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,"Error de conexión",Toast.LENGTH_LONG).show()
                // Failed to read value
                Log.i("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    fun saveUsuarioToFirebase(){

    }

    fun loadHome(withInternet: Boolean){

       if (userData.user.uid.isNullOrEmpty()){
            loadRegisterActivity()
        }

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
        if (!withInternet){
            Toast.makeText(this,"No se han podido actualizar los datos",Toast.LENGTH_LONG).show()
        }
        mainUILoaded = true
    }

    fun loadRegisterActivity(){
        val intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
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
*/

    fun actualizarPuntuacion(view: View){
        userData.changeThemeScore(5,1,"geografia")
    }
/*
    fun actualizarTemas(view: View){
        var temas: TemasList = TemasList()
        temas.temas.add(Tema("Geografía","geografia","#80CBC4","#00BFA5"))
        temas.temas.add(Tema("Tecnología","tecnologia","#81D4FA","#0091EA"))
        temas.temas.add(Tema("Animales","animales","#FFCC80","#FF6D00"))
        userData.ActualizarTemas(temas)
        temasData.addUpdateTemas(temas)
        retriveTemas()
    }*/

    fun retriveTemas(){
        userData.user.temas[User.DificultadEnum.FACIL.value].forEach {themeScore ->
            Log.i("TAG","Temas faciles: ${themeScore.name} , dificultad: ${themeScore.dificultadid}")
        }
        userData.user.temas[User.DificultadEnum.MEDIO.value].forEach {themeScore ->
            Log.i("TAG","Temas medios: ${themeScore.name}, dificultad: ${themeScore.dificultadid}")
        }
        userData.user.temas[User.DificultadEnum.DIFICIL.value].forEach {themeScore ->
            Log.i("TAG","Temas dificiles: ${themeScore.name} , dificultad: ${themeScore.dificultadid}")
        }
        Log.i("TAG","Lista de temas: ")
        temasData.lista.temas.forEach {
            Log.i("TAG","Tema ${it.name}")
        }
    }
    /*
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
        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
    }

    companion object {
        var conectionState = false
        val USER_DATA = "userData"
        val TEMAS_DATA = "temasData"
        var userData = UserData()
        var temasData = TemaData()
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

    override fun saveListaTemas(temas: TemasList) {
        val prefsEditor = sharedPreferences?.edit()
        val json = gson?.toJson(temas)
        prefsEditor?.putString(TEMAS_DATA,json)
        prefsEditor?.apply()
    }

    override fun getListaTemas() {
        if (sharedPreferences!!.contains(USER_DATA)){
            val json = sharedPreferences?.getString(TEMAS_DATA,"")
            temasData.lista = gson!!.fromJson(json,TemasList::class.java)
        }
    }
}
