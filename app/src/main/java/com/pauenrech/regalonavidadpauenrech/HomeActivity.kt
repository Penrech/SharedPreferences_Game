package com.pauenrech.regalonavidadpauenrech


import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.transition.Scene
import android.support.transition.Transition
import android.support.transition.TransitionInflater
import android.support.transition.TransitionManager

import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_home.*
import com.google.gson.Gson
import com.pauenrech.regalonavidadpauenrech.model.*
import kotlinx.android.synthetic.main.content_home.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener


class HomeActivity : AppCompatActivity(),
    UserData.SaveAndGetLocalUserData,
    TemaData.SaveOrGetListaTemas,
    PreguntasData.SaveAndGetListaPreguntas{



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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        gson = Gson()
        userData.savingInterface = this as UserData.SaveAndGetLocalUserData
        temasData.savingInterface = this as TemaData.SaveOrGetListaTemas
        preguntasData.savingInterface = this as PreguntasData.SaveAndGetListaPreguntas

        rootHome.background = getDrawable(android.R.color.background_light)

        homeScene = Scene.getSceneForLayout(rootHome,R.layout.activity_home,this)
        loadingScene = Scene.getSceneForLayout(rootHome,R.layout.loading_home,this)

        loadingScene?.enter()

        database = FirebaseDatabase.getInstance()

        conectionRef = database?.getReference(".info/connected")
        preguntasRef = database?.getReference("preguntas")!!.child("ES_es")
        temasRef = database?.getReference("temas")!!.child("ES_es")
        usuariosRef = database?.getReference("usuarios")
        dummyRef = database?.getReference("conection")

        userData.getLocalUserData()
        temasData.getLocalListaTemas()
        preguntasData.getLocalPreguntasData()

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
        getPreguntasFromFirebase()
        saveUsuarioToFirebase(userData.user)
        setTimer(2)

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
                    checkInternetAndUser(true)
                }
                else{
                    checkInternetAndUser(false)
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
                Log.i("FIREBASE","Numero de hijos temas: ${dataSnapshot.childrenCount}")
                for ( i in dataSnapshot.children){
                    temasList.temas.add(i.getValue(Tema::class.java)!!)
                }
                temasData.addUpdateTemas(temasList)
                userData.ActualizarTemas(temasList)
                //retriveTemas()

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,getString(R.string.error_conexion),Toast.LENGTH_LONG).show()
                // Failed to read value
                Log.i("TAG", "Failed to read value.", error.toException())
            }
        })

    }

    fun getPreguntasFromFirebase(){
        // Read from the database
        preguntasRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val preguntasList = PreguntasTotal()
                    Log.i("FIREBASE","Numero de hijos preguntas: ${dataSnapshot.childrenCount}")
                for (i in dataSnapshot.children){
                    Log.i("FIREBASE","Clave : ${i.key} , Valor : ${i.value}")
                    preguntasList.totalPreguntas.add(i.getValue(PreguntasDificultad::class.java)!!)
                }
                preguntasData.addUpdatePreguntas(preguntasList)
                retrivePreguntas()
                if (!mainUILoaded)
                    checkInternetAndUser(true)

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,getString(R.string.error_conexion),Toast.LENGTH_LONG).show()
                // Failed to read value
                Log.i("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    fun saveUsuarioToFirebase(user: User){
        if (user.uid != null){
            usuariosRef?.child(user.uid!!)?.setValue(user)
        }
    }

    fun checkInternetAndUser(withInternet: Boolean){

       if (userData.user.uid.isNullOrEmpty()){
            loadRegisterActivity()
        }
        else{
           loadHomeUi()
       }

        if (!withInternet){
            Toast.makeText(this,getString(R.string.error_no_conection_no_data),Toast.LENGTH_LONG).show()
        }

        mainUILoaded = true
    }

    fun loadHomeUi(){
        Log.i("TAG","printo la home")
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

    fun loadRegisterActivity(){
        val intent = Intent(this,RegisterActivity::class.java)
        startActivityForResult(intent,21)
    }

    fun loadGameSelectionActivity(view: View){

        if (temasData.lista.temas.isEmpty()){
            var snack = Snackbar.make(rootHome,getString(R.string.error_no_game_data),Snackbar.LENGTH_LONG)
            var snackbarView = snack.view
            snackbarView.setBackgroundColor(getColor(R.color.colorPrimary))
            var snackTextView = snackbarView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
            snackTextView.setTextColor(getColor(android.R.color.background_light))
            snack.setActionTextColor(getColor(android.R.color.background_light))
            snack.setAction(getString(R.string.error_restart_app)) {
                val i = baseContext.packageManager
                    .getLaunchIntentForPackage(baseContext.packageName)
                i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
            snack.show()
        }
        else{
            val intent = Intent(this,SelectionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 21 && resultCode == Activity.RESULT_OK){
            userData.changeNickname(data?.getStringExtra("nickname")!!)
            userData.setUid(data.getStringExtra("uid"))
            loadHomeUi()
        }

        super.onActivityResult(requestCode, resultCode, data)
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
    fun retrivePreguntas(){
        preguntasData.listaPreguntasTotal.totalPreguntas[0].temas.forEach {
            Log.i("PREGUNTAS","Preguntas de ${it.name}")
            it.preguntas.forEach {
                Log.i("PREGUNTAS",it.respuesta_correcta)
            }
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
        //overridePendingTransition(0,0)
        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
    }

    companion object {
        var conectionState = false
        val USER_DATA = "userData"
        val TEMAS_DATA = "temasData"
        val PREGUNTAS_DATA = "preguntasData"
        var userData = UserData()
        var temasData = TemaData()
        var preguntasData = PreguntasData()
        var sharedPreferences: SharedPreferences? = null
        var gson : Gson? = null
    }

    override fun saveUserData(user: User) {
        val prefsEditor = sharedPreferences?.edit()
        val json = gson?.toJson(user)
        prefsEditor?.putString(USER_DATA,json)
        prefsEditor?.apply()
        saveUsuarioToFirebase(user)
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
        if (sharedPreferences!!.contains(TEMAS_DATA)){
            val json = sharedPreferences?.getString(TEMAS_DATA,"")
            temasData.lista = gson!!.fromJson(json,TemasList::class.java)
        }
    }

    override fun savePreguntas(preguntas: PreguntasTotal) {
        val prefsEditor = sharedPreferences?.edit()
        val json = gson?.toJson(preguntas)
        prefsEditor?.putString(PREGUNTAS_DATA,json)
        prefsEditor?.apply()
    }

    override fun getPreguntas() {
        if (sharedPreferences!!.contains(PREGUNTAS_DATA)){
            val json = sharedPreferences?.getString(PREGUNTAS_DATA,"")
            preguntasData.listaPreguntasTotal = gson!!.fromJson(json,PreguntasTotal::class.java)
        }
    }
}
