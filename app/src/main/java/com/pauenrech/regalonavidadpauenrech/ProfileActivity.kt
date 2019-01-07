package com.pauenrech.regalonavidadpauenrech

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.opengl.Visibility
import android.support.v4.widget.NestedScrollView
import android.view.View
import kotlinx.android.synthetic.main.activity_profile.*
import android.widget.TextView
import com.pauenrech.regalonavidadpauenrech.tools.NicknameValidator
import android.view.WindowManager
import android.os.Build




class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /**
         *
         * Aqui llamo a una función que a su vez llama a una función para validar el texto del editText nickname
         *
         * */
        validarTextoNickname()

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


    }

    fun validarTextoNickname(){
        profileNickName.addTextChangedListener(object : NicknameValidator(profileNickName) {
            override fun validate(textView: TextView, text: String) {
               if (text.length < 2){
                   profileNicknameErrorLabel.visibility = View.VISIBLE
                   profileNicknameErrorLabel.text = getString(R.string.profile_nickname_error_too_short)
               }
               else{
                    if (profileNicknameErrorLabel.visibility == View.VISIBLE){
                        profileNicknameErrorLabel.visibility = View.GONE
                    }
               }
            }
        })
    }

    /**
     *
     * Se cierra la activity sin animaciones de salida ni entrada
     *
     * */
    override fun finish() {
        super.finish()
        overridePendingTransition(0,0)
    }
}
