package com.pauenrech.regalonavidadpauenrech

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_profile.*
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import com.google.firebase.database.*
import com.pauenrech.regalonavidadpauenrech.adapters.profileSpecificListAdapter
import com.pauenrech.regalonavidadpauenrech.model.User
import com.pauenrech.regalonavidadpauenrech.layoutManagers.NoScrollLinearLayoutManager

class ProfileActivity : AppCompatActivity() {

    private var layoutManager: NoScrollLinearLayoutManager? = null
    private var adapter: RecyclerView.Adapter<profileSpecificListAdapter.SpecificListViewHolder>? = null

    private var initialSpecificData : List<MutableList<User.ThemeScore>>? = null
    private var initialDificultad : Int? = null

    private val userDataReference = HomeActivity.userData
    private var database : FirebaseDatabase? = null
    private var usersRef: DatabaseReference? = null
    var nicknameChanged: Boolean = true
    var handler: Handler? = null
    var runnable: Runnable? = null

    private var imm : InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        database = FirebaseDatabase.getInstance()
        usersRef = database?.getReference("usuarios")

        handler = Handler()
        runnable = Runnable {
            if (!nicknameChanged){
                imm?.hideSoftInputFromWindow(profileNickNameEdit.windowToken, 0)

                Toast.makeText(this,getString(R.string.error_no_conection_no_data),Toast.LENGTH_LONG).show()
                setNicknameNoEditable(true)
            }
        }

        /**
         *
         * Inserto el valor de los datos del usuario en sus correspondientes lugares, extraigo los datos del homeActivity
         * actividad que utilizo para gestionar la "base de datos local" con shared preferences
         *
         * */
        profileNickName.text = userDataReference.user.nickname
        profile_seekBar.progress = userDataReference.user.dificultad
        profilePuntuacionGlobalText.text = "${userDataReference.user.puntuacion}"

        initialSpecificData = userDataReference.user.temas
        initialDificultad = userDataReference.user.dificultad

        setSpecificRecyclerView()
        setSeekBarListener()

        /**
         *
         * Aquí compruebo si el valor de ranking es -1 antes de añadir el valor al textView. Esto es asi ya que el ranking
         * se obtiene de la base de datos online, y por fallos de conexión en un primer momento puede no aparecer nada.
         * No utilizo un nullable para hacer la implementación más sencilla
         *
         *
         * */
        if (userDataReference.user.ranking != -1){
            profileRanking.text = "${userDataReference.user.ranking}"
        }
        else{
            profileRanking.text = "-"
        }

        /**
         *
         * Aqui implemento la variable imm para gestionar cuando se muestra o no el teclado
         *
         * */
        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        /**
         *
         * En estas tres lineas de código se añade un icono al toolbar para permitir volver a la activity anterior
         * Se cambia también el título que aparecerá en el toolbar
         *
         *
         * */
        profile_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        profile_toolbar.setTitle(R.string.profile_activity_label)

        profile_toolbar.setNavigationOnClickListener { onBackPressed() }

        /**
         *
         * Este onClick esta unido al botón que permite editar el nickname del usuario. Ya que es un solo botón que realiza
         * dos acciones, compruebo con un if en que estado se está
         *
         * */
        profileChangeNicknameBtn.setOnClickListener {
            /**
             *
             * Si el textView no editable esta visible, se realiza lo siguiente:
             * 1. Se añade el valor del textview al editText donde se editará el nickname
             * 2. Se cambia el icono del propio botón para que ahora represente validación en lugar de edición
             * 3. Se oculta el textview no editable y se muestra el editText
             * 4. Se centra el focus en este edittext
             * 5. Se abre el teclado del movil
             *
             * */
            if (profileNickName.visibility == View.VISIBLE){
                nicknameChanged = false

                profileNickNameEdit.setText(profileNickName.text.toString())
                profileChangeNicknameBtn.setImageResource(R.drawable.ic_round_done_24px_white)

                profileNickName.visibility = View.GONE
                profileNickNameEdit.visibility = View.VISIBLE
                profileNickNameEdit.requestFocus()

                imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }

            /**
             *
             * En este caso se llama a una función que implementa to_do el código para hacer el código más legible
             *
             * */
            else{
                validarTextoNickname()
            }
        }
    }

    private fun setSpecificRecyclerView(){
        if (initialSpecificData!![0].size > 1){
            layoutManager = NoScrollLinearLayoutManager(this)
            adapter = profileSpecificListAdapter(initialSpecificData!![initialDificultad!!])

            profileSpecificsRV.adapter = adapter
            profileSpecificsRV.layoutManager = layoutManager
        }
        else{
            profileSpecificScoreLabel.visibility = View.GONE
            profileSpecificCardView.visibility = View.GONE
        }
    }

    private fun setSeekBarListener(){
        profile_seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (profileSpecificCardView.visibility == View.VISIBLE){
                    val recyclerAdapter = profileSpecificsRV.adapter!! as profileSpecificListAdapter
                    recyclerAdapter.changeData(initialSpecificData!![i])
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    /**
     *
     * Esta función se encarga de validar si lo introducido en el editText sigue una serie de normas.
     *
     * */
    private fun validarTextoNickname(){
        when {
            profileNickNameEdit.text.length < 2 -> {
                profileNicknameErrorLabel.visibility = View.VISIBLE
                profileNicknameErrorLabel.text = getString(R.string.profile_nickname_error_too_short)
            }
            profileNickNameEdit.text.toString() == userDataReference.user.nickname -> setNicknameNoEditable(true)
            else -> validateWithFirebase(profileNickNameEdit.text.toString())
        }
    }

    fun setNicknameNoEditable(error: Boolean){
        if (profileNicknameErrorLabel.visibility == View.VISIBLE){
            profileNicknameErrorLabel.visibility = View.GONE
        }

        profileChangeNicknameBtn.visibility = View.VISIBLE
        profileNicknameLoading.visibility = View.GONE

        if (!error) {
            userDataReference.changeNickname(profileNickNameEdit.text.toString())
        }

        profileNickName.text = userDataReference.user.nickname
        profileChangeNicknameBtn.setImageResource(R.drawable.ic_round_edit_24px_white)

        profileNickName.visibility = View.VISIBLE
        profileNickNameEdit.visibility = View.GONE

        imm?.hideSoftInputFromWindow(profileNickNameEdit.windowToken, 0)

        nicknameChanged = true
    }

    private fun validateWithFirebase(newNickname: String){
        profileChangeNicknameBtn.visibility = View.INVISIBLE
        profileNicknameLoading.visibility = View.VISIBLE

        setTimer(2)

        val lowerCaseNickname = newNickname.toLowerCase()

        usersRef?.orderByChild("nicknameLowerCase")?.equalTo(lowerCaseNickname)?.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!nicknameChanged){
                    if (dataSnapshot.childrenCount > 0){
                        profileChangeNicknameBtn.visibility = View.VISIBLE
                        profileNicknameLoading.visibility = View.GONE
                        profileNicknameErrorLabel.text = getString(R.string.error_nickname_already_in_use)
                        profileNicknameErrorLabel.visibility = View.VISIBLE

                        handler?.removeCallbacks(runnable)
                    }
                    else{
                       setNicknameNoEditable(false)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (!nicknameChanged) {
                    Toast.makeText(this@ProfileActivity, getString(R.string.error_conexion), Toast.LENGTH_LONG).show()
                    setNicknameNoEditable(true)
                }
                Log.i("CONECTION","Error de conexión obteniedo datos usuario ${error.toException()}")
            }
        })
    }

    private fun setTimer(seconds: Int){
        nicknameChanged = false
        val miliseconds = (seconds * 1000).toLong()

        handler!!.postDelayed(runnable,miliseconds)
    }

    override fun onBackPressed() {
        if (!nicknameChanged){
            setNicknameNoEditable(true)
        }
        else{
            super.onBackPressed()
        }
    }

    override fun finish() {
        handler?.removeCallbacks(runnable)

        userDataReference.changeDificultad((profile_seekBar.progress))

        imm?.hideSoftInputFromWindow(profileNickNameEdit.windowToken, 0)

        super.finish()

        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }

    override fun onPause() {
        userDataReference.changeDificultad((profile_seekBar.progress))

        imm?.hideSoftInputFromWindow(profileNickNameEdit.windowToken, 0)

        super.onPause()
    }
}
