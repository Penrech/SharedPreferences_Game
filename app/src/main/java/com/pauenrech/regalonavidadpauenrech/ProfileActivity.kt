package com.pauenrech.regalonavidadpauenrech

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.support.v4.widget.NestedScrollView
import android.view.View
import kotlinx.android.synthetic.main.activity_profile.*


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


        /**
         *
         * Añado un listener al nestedScrollView de la activity para detectar cuando este scroll esta arriba del to-do
         * Si el scroll está arriba del to_do, el toolbar no presenta sombra, si no es así, si la presenta.
         * He añadido un If adicional en dentro del else para evitar que la propiedad se reasigne cada vez que se
         * hace scroll
         *
         * */
        profileScroll.setOnScrollChangeListener { nestedScrollView: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->

            if (scrollY == 0) {
                profileAppBar.elevation = 0f
            }
            else{
                if (profileAppBar.elevation == 0f){
                    profileAppBar.elevation = 5f
                }
            }

        }
    }

    fun validarTextoNickname(){

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
