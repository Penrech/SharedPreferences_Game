package com.pauenrech.regalonavidadpauenrech

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_profile.*
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import com.pauenrech.regalonavidadpauenrech.adapters.profileSpecificListAdapter
import com.pauenrech.regalonavidadpauenrech.data.User
import com.pauenrech.regalonavidadpauenrech.layoutManagers.NoScrollLinearLayoutManager


class ProfileActivity : AppCompatActivity() {


    private var layoutManager: NoScrollLinearLayoutManager? = null
    private var adapter: RecyclerView.Adapter<profileSpecificListAdapter.SpecificListViewHolder>? = null
    var initialSpecificData : List<MutableList<User.ThemeScore>>? = null
    var initialDificultad : Int? = null

    /**
     *
     * Declaro una variable input manager para gestionar más adelante cuando se muestra o oculta el teclado
     *
     * */
    var imm : InputMethodManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /**
         *
         * Inserto el valor de los datos del usuario en sus correspondientes lugares, extraigo los datos del homeActivity
         * actividad que utilizo para gestionar la "base de datos local" con shared preferences
         *
         * */

        profileNickName.text = HomeActivity.userData.user.nickname
        profile_seekBar.progress = HomeActivity.userData.user.dificultad
        profilePuntuacionGlobalText.text = "${HomeActivity.userData.user.puntuacion}"
        initialSpecificData = HomeActivity.userData.user.temas
        initialDificultad = HomeActivity.userData.user.dificultad

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

        if (HomeActivity.userData.user.ranking != -1){
            profileRanking.text = "${HomeActivity.userData.user.ranking}"
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

    fun setSpecificRecyclerView(){

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

    fun setSeekBarListener(){
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
    fun validarTextoNickname(){
               if (profileNickNameEdit.text.length < 2){
                   profileNicknameErrorLabel.visibility = View.VISIBLE
                   profileNicknameErrorLabel.text = getString(R.string.profile_nickname_error_too_short)
               }
               //todo validar con firebase
               else{
                    if (profileNicknameErrorLabel.visibility == View.VISIBLE){
                        profileNicknameErrorLabel.visibility = View.GONE
                    }
                    HomeActivity.userData.changeNickname(profileNickNameEdit.text.toString())
                    profileNickName.text = HomeActivity.userData.user.nickname
                    profileChangeNicknameBtn.setImageResource(R.drawable.ic_round_edit_24px_white)
                    profileNickName.visibility = View.VISIBLE
                    profileNickNameEdit.visibility = View.GONE
                    imm?.hideSoftInputFromWindow(profileNickNameEdit.windowToken, 0);
               }
    }

    /**
     *
     * Se cierra la activity sin animaciones de salida ni entrada
     *
     * */
    override fun finish() {
        HomeActivity.userData.changeDificultad((profile_seekBar.progress))
        imm?.hideSoftInputFromWindow(profileNickNameEdit.windowToken, 0);
        super.finish()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }

    override fun onPause() {
        HomeActivity.userData.changeDificultad((profile_seekBar.progress))
        imm?.hideSoftInputFromWindow(profileNickNameEdit.windowToken, 0);
        super.onPause()
    }

}
