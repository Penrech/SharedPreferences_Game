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


/**
*
* Aquí se instancian 3 interfaces que sirven para obtener información de eventos de diversas clases modelo
*
* Es la clase principal, se utiliza como lazo de unión entre la base de datos local en shared preferences y la
* aplicación
*
* */
class HomeActivity : AppCompatActivity(),

    UserData.SaveAndGetLocalUserData,
    TemaData.SaveOrGetListaTemas,
    PreguntasData.SaveAndGetListaPreguntas{

    /**
     *
     * @param loadingScene Escena inicial, cuando la app está cargando
     * @param homeScene Escena de la actividad home una vez cargada
     *
     */
    var loadingScene : Scene? = null
    var homeScene: Scene? = null

    /**
     *
     * Todos estos parámetros son para referenciar la app con la base de datos externa Firebase
     * @param database es el la referencia principal con la base de datos
     * @param conectionRef es la referencia con un objeto de la base de datos que informa sobre la conexión
     * @param preguntasRef es la referencia con el bloque de preguntas guardado en la base de datos
     * @param temasRef es la referencia con el bloque de temas guardado en la base de datos
     * @param usuariosRef es la referencia con el bloque de usuarios guardado en la base de datos
     *
     * */
    var database : FirebaseDatabase? = null
    var conectionRef: DatabaseReference? = null
    var preguntasRef: DatabaseReference? = null
    var temasRef: DatabaseReference? = null
    var usuariosRef: DatabaseReference? = null

    /**
     *
     * @param mainUILoaded es un booleano que indica si la UI de home ha sido cargada o si todavía está en
     * la escena loadingScene
     *
     * */
    var mainUILoaded: Boolean = false

    /**
     *
     * Objecto estático de la clase HomeActivity
     *
     * @param conectionState es un boleano donde se guarda el estado de la conexión de la app con su base de datos
     * online : (true -> conectado) (false -> desconectada).
     * False (desconectada) como valor inicial
     *
     * @param USER_DATA define un string que se utiliza como clave para los objetos de clase
     * @see User en la base de datos local
     *
     * @param TEMAS_DATA define un string que se utiliza como clave para los objetos de clase
     * @see TemasList en la base de datos local
     *
     * @param PREGUNTAS_DATA define un string que se utiliza como clave para los objetos de clase
     * @see PreguntasTotal en la base de datos local
     *
     * @param userData almacena una instancia de la clase
     * @see UserData en la que se almacenan los datos del usuario mientras la aplicación está corriendo
     *
     * @param temasData almacena una instancia de la clase
     * @see TemaData en la que se almacenan los datos de los temas mientras la aplicación está corriendo
     *
     * @param preguntasData almacena una instancia de la clase
     * @see PreguntasData en la que se almacenan los datos de los temas mientras la aplicación está corriendo
     *
     * @param sharedPreferences declara una variable de la clase SharedPreferences, para utilizarla como referencia
     * con la base de datos SharedPreferences
     *
     * @param gson declara una variable de la clase de Google Gson, la cual permite hacer parse de Json en String y
     * viceversa
     *
     * */
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

    override fun onCreate(savedInstanceState: Bundle?) {

        /**
         *
         * Se hereda la funcionalidad de la función onCreate
         *
         * Se establece como vista el documento xml R.layout.activity_home (activity_home.xml)
         *
         * */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /**
         *
         * Se inicializa la variable sharedPrefences, asociandola con la base de datos SharedPreferences
         * @see sharedPreferences
         *
         * Se inicializa la variable gson con una instancia de la clase
         * @see gson
         * @see Gson
         *
         * */
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        gson = Gson()

        /**
         *
         * Esto es un grupo de inicialización de las tres interfaces incorporadas en HomeActivity
         *
         * Dentro de cada clase hay declarada una interfaz en la que se declaran métodos para guardar datos
         * @see UserData
         * @see TemaData
         * @see PreguntasData
         *
         * */
        userData.savingInterface = this as UserData.SaveAndGetLocalUserData
        temasData.savingInterface = this as TemaData.SaveOrGetListaTemas
        preguntasData.savingInterface = this as PreguntasData.SaveAndGetListaPreguntas

        /**
         *
         * Se declara el color del fondo de la Activity HomeActivity de forma inicial en color blanco
         *
         * */
        rootHome.background = getDrawable(android.R.color.background_light)

        /**
         *
         * Se inicializan las dos escenas :
         * @see homeScene y
         * @see loadingScene
         * con sus correspondientes layouts y se establece LoadingScene como escena inicial
         *
         * */
        homeScene = Scene.getSceneForLayout(rootHome,R.layout.activity_home,this)
        loadingScene = Scene.getSceneForLayout(rootHome,R.layout.loading_home,this)
        loadingScene?.enter()

        /**
         *
         * En este grupo se inicializan las variables y referencias con la base de datos online (Firebase)
         *
         * Se inicializa la variable
         * @see database con una instancia de la clase
         * @see FirebaseDatabase
         *
         * Se inicializan las siguientes variables con sus respectivas referencias en la base de datos
         * @see conectionRef
         * @see preguntasRef
         * @see temasRef
         * @see usuariosRef
         *
         * */
        database = FirebaseDatabase.getInstance()
        conectionRef = database?.getReference(".info/connected")
        preguntasRef = database?.getReference("preguntas")!!.child("ES_es")
        temasRef = database?.getReference("temas")!!.child("ES_es")
        usuariosRef = database?.getReference("usuarios")

        /**
         *
         * Se inicializan las tres variables que guardan datos extraidos de la base de datos local:
         *
         * @see userData con la función
         * @sample getUserData que deriva a través de una interfaz de la función
         * @see UserData.getLocalUserData
         *
         * @see temasData con la función
         * @sample getListaTemas que deriva a través de una interfaz de la función
         * @see TemaData.getLocalListaTemas
         *
         * @see preguntasData con la función
         * @sample getPreguntas que deriva a través de una interfaz de la función
         * @see PreguntasData.getLocalPreguntasData
         *
         * */
        userData.getLocalUserData()
        temasData.getLocalListaTemas()
        preguntasData.getLocalPreguntasData()

        /**
         *
         * @param getConexionFromFirebase inicializa un listener para comprobar cuando hay o no hay conexión con la
         * base de datos
         * @sample getConexionFromFirebase
         *
         * @param getTemasFromFirebase hace una consulta con la base de datos para descargar los temas de esta
         * @sample getTemasFromFirebase
         *
         * @param getPreguntasFromFirebase hace una consulta con la base de datos para descargar las preguntas de esta
         * @sample getPreguntasFromFirebase
         *
         * @param saveUsuarioToFirebase actualiza de forma inicial la información del usuario en la base de datos local
         * con la base de datos Firebase
         * Se le pasa un parámetro de la clase [User] , que corresponde a los datos del usuario
         * @sample saveUsuarioToFirebase
         *
         * @param setTimer inicializa una función que incorpora una cuenta atrás, esta determina cuanto hay que esperar
         * antes de declarar que no hay conexión si la base de datos no responde en el tiempo en segundos pasado a través
         * de un parámetro [Int]
         * @sample setTimer
         *
         * */
        getConexionFromFirebase()
        getTemasFromFirebase()
        getPreguntasFromFirebase()
        saveUsuarioToFirebase(userData.user)
        setTimer(2)

    }

    /**
     *
     * Implementación de la función
     * @see setTimer
     * @property seconds segundos de espera antes de declarar la app sin conexión
     *
     * */
    fun setTimer(seconds: Int){
        /**
         *
         * @param miliseconds convierte la
         * @property seconds en milisegundos
         *
         * La instancia de la clase
         * @see Handler iniciará una tarea
         * @see Runnable al pasar el tiempo especificado por la
         * @property seconds
         *
         * Seguidamente se comprueba si:
         *
         * @see conectionState es [true], si la conexión ya ha sido establecida
         * si conectionState es [true] se comprueba si la interfaz principal
         * @see mainUILoaded ha sido cargada o no
         * Si no ha sido cargada se llama a la función
         * @see checkInternetAndUser con su propiedad
         * @property withInternet con el valor [true]
         *
         * @see conectionState es [false], si la conexión no  ha sido establecida
         * si conectionState es [false] se llama a la función
         * @see checkInternetAndUser con su propiedad
         * @property withInternet con el valor [false]
         *
         * */
        val miliseconds = (seconds * 1000).toLong()
        Handler().postDelayed(
            {
                if (conectionState){
                    if (!mainUILoaded)
                        /**
                         *
                         * @see checkInternetAndUser comprueba si en la base de datos local hay ya un usuario
                         * registrado y realiza o no una determinada tarea si hay o no conexión
                         * @sample checkInternetAndUser
                         *
                         * */
                        checkInternetAndUser(true)
                }
                else{
                    checkInternetAndUser(false)
                }
            },
            miliseconds
        )
    }

    /**
     *
     * @see getConexionFromFirebase no tiene propiedad de entrada, añade un listener que escucha si la base de datos
     * Firebase está conectada o no. Info: Si la aplicación no guarda ni lee nada de esta base de datos en 60 segundos
     * esta se desconecta del cliente para ahorrar bateria. Se conecta de nuevo automaticamente cuando la aplicación
     * lo requiera, siempre que halla conexión a internet
     *
     * */
    fun getConexionFromFirebase(){
        /**
         *
         * El listener se le añade a la referencia
         * @see conectionRef
         * Cuando hay un cambio de conexión, se actualiza el parámetro
         * @see conectionState en consecuencia
         *
         * */
        conectionRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    conectionState = connected
                    Log.i("CONECTION","Conectado")
                } else {
                    conectionState = connected
                    Log.i("CONECTION","No conectado")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("CONECTION","Error detectando conexion ${error.toException()}")
            }
        })
    }

    /**
     *
     * @see getTemasFromFirebase no tiene propieda de entrada, descarga una única vez por instancia(no de forma continuada)
     * los temas disponibles en la base de datos Firebase
     *
     * */
    fun getTemasFromFirebase(){

        /**
         *
         * Se obtienen entos datos a través de la referencia
         * @see temasRef
         *
         * */
        temasRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                /**
                 *
                 * Se crea una variable
                 * @param temasList de la clase
                 * @see TemasList en la que se irán almacenando todos los temas
                 * Se añaden a través de un loop temas de la clase
                 * @see Tema
                 *
                 * Se guardan estos temas en la variable
                 * @see temasData con la función
                 * @see TemaData.addUpdateTemas pasandole como parámetro la variable
                 * @see temasList
                 *
                 * Se guardan estos temas como parte de los datos del usuario en la variable
                 * @see userData con la función
                 * @see UserData.ActualizarTemas pasandole como parámetro la variable
                 * @see temasList
                 *
                 * */
                val temasList = TemasList()

                for ( i in dataSnapshot.children){
                    temasList.temas.add(i.getValue(Tema::class.java)!!)
                }

                temasData.addUpdateTemas(temasList)
                userData.ActualizarTemas(temasList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,getString(R.string.error_conexion),Toast.LENGTH_LONG).show()

                Log.i("CONECTION","Error de conexión obteniedo temas ${error.toException()}")
            }
        })

    }

    /**
     *
     * @see getPreguntasFromFirebase no tiene propieda de entrada, descarga una única vez por instancia(no de forma continuada)
     * las prguntas disponibles en la base de datos Firebase
     *
     * */
    fun getPreguntasFromFirebase(){

        /**
         *
         * Se obtienen estos datos a través de la referencia
         * @see preguntasRef
         *
         * */
        preguntasRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                /**
                 *
                 * Se crea una variable
                 * @param preguntasList de la clase
                 * @see PreguntasTotal en la que se irán almacenando todas las preguntas
                 * Se añaden a través de un loop preguntas de la clase
                 * @see PreguntasDificultad
                 *
                 * Se guardan estas preguntas en la variable
                 * @see preguntasData con la función
                 * @see PreguntasData.addUpdatePreguntas pasandole como parámetro la variable
                 * @see preguntasList
                 *
                 * Una vez cargados estos datos (suelen ser los de mayor volumen) se da por hecho que todos los datos
                 * de la base de datos han sido descargados.
                 * Se comprueba si la interfaz de Home referente a la escena
                 * @see homeScene ha sido cargada, comprobando el parámetro
                 * @see mainUILoaded
                 *
                 * Si mainUILoaded tiene como valor [false] (no ha sido cargada) se llama a la función
                 * @see checkInternetAndUser con el valor [true] (si hay internet) y comprueba si en la base de datos
                 * local hay ya un usuario registrado y realiza o no una determinada tarea si hay o no conexión
                 * @sample checkInternetAndUser
                 *
                 * */
                val preguntasList = PreguntasTotal()

                for (i in dataSnapshot.children){
                    preguntasList.totalPreguntas.add(i.getValue(PreguntasDificultad::class.java)!!)
                }

                preguntasData.addUpdatePreguntas(preguntasList)
                if (!mainUILoaded)
                    checkInternetAndUser(true)

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,getString(R.string.error_conexion),Toast.LENGTH_LONG).show()

                Log.i("CONECTION","Error de conexión obteniedo preguntas ${error.toException()}")
            }
        })
    }

    /**
     *
     * @see saveUsuarioToFirebase añade un usuario a la base de datos Firebase
     * toma como propiedad
     * @property user que es de la clase
     * @see User
     *
     * */
    fun saveUsuarioToFirebase(user: User){

        /**
         *
         * Comprueba si el id guardado en
         * @see User.uid es diferente de null. Si este valor es null significa que no hay ningún usuario registrado
         * actualmente en la base de datos local de la aplicación
         *
         * Si no es null se procede a guardar los datos del usuario de la base de datos local en la base de datos Firebase
         * para ello se utiliza la referencia de usuarios con la base de datos Firebase
         * @see usuariosRef
         * y se le añade el valor
         * @property user
         *
         * */
        if (user.uid != null){
            usuariosRef?.child(user.uid!!)?.setValue(user)
        }
    }

    /**
     *
     * @see checkInternetAndUser comprueba básicamente si hay o no hay ya un usuario registrado en la base de datos
     * local de la app en el dispositivo. Además informa al usuario si hay o no conexión
     * Recibe como parámetro la propiedad
     * @property withInternet que indica si hay conexión [true] o si no [false]
     *
     * */
    fun checkInternetAndUser(withInternet: Boolean){

        /**
         *
         * Se comprueba si hay un usuario registrado en la base de datos local mirando la variable que guarda los
         * datos de usuario obtenidos de la base de datos local
         * @see userData , concretamente se comprueba si el parámetro uid de la instacia de su clase
         * @see User.uid es null
         *
         * Si es null, se llama a la función
         * @see loadRegisterActivity que se encarga de cargar la activity de la clase
         * @see RegisterActivity con el fin de que el usuario se registre en la base de datos local
         *
         * Si no es null, se llama a la función
         * @see loadHomeUi que se encarga de cargar la interfaz de la activity
         * @see HomeActivity en su escena
         * @see homeScene
         *
         * See comprueba la propiedad
         * @property withInternet para ver si hay internet o no
         * si no hay internet, si el valor de esta propiedad es [false] se muestra al usuario un
         * @see Toast informandole de este estado
         *
         *
         *
         * */
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
            R.id.action_ranking ->{
                Toast.makeText(this,getString(R.string.development_not_implemented),Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_help ->{
                Toast.makeText(this,getString(R.string.development_not_implemented),Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




    override fun saveUserData(user: User) {
        val prefsEditor = sharedPreferences?.edit()
        val json = gson?.toJson(user)
        prefsEditor?.putString(USER_DATA,json)
        prefsEditor?.apply()
        saveUsuarioToFirebase(user)
    }

    override fun updateMainPuntuacionTextView(puntuacion: Int) {
        homeHighScore.text = "$puntuacion"
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
