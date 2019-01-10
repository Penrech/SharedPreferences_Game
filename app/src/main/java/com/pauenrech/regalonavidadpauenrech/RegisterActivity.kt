package com.pauenrech.regalonavidadpauenrech

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_register.*
import android.graphics.Typeface
import android.view.LayoutInflater
import android.widget.TextView
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*


class RegisterActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun registrarse(){
        if (registerNickName.text.length < 2){
            registerNicknameErrorLabel.visibility = View.VISIBLE
            registerNicknameErrorLabel.text = getString(R.string.profile_nickname_error_too_short)
        }
        //todo validar con firebase
        else{
            HomeActivity.userData.changeNickname(registerNickName.text.toString())
        }
    }

    private fun showCreateListDialog(){

        val dialogLista = AlertDialog.Builder(this).create()


        //Title
        val customView = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog,null)
        customView.customDialogTitle.text = "¿Salir de la App?"
        customView.customDialogMessage.text = "Es necesario registrarse para acceder a la aplicación"

        //dialogLista.setMessage("Es necesario registrarse para acceder a la aplicación")
        /*dialogLista.setPositiveButton("Salir") {dialog, which ->
            finishAndRemoveTask()
        }
        dialogLista.setNegativeButton("Cancelar") {dialog, which ->
        }*/
        customView.customDialogPrimaryButton.text = "Salir"
        customView.customDialogSecundaryButton.text = "Cancelar"
        customView.customDialogPrimaryButton.setOnClickListener {
            finishAndRemoveTask()
            dialogLista.dismiss()
        }
        customView.customDialogSecundaryButton.setOnClickListener {
            dialogLista.dismiss()
        }
        dialogLista.setView(customView)
        dialogLista.window.setBackgroundDrawable(getDrawable(android.R.color.transparent))
        dialogLista.show()


    }

    override fun onBackPressed() {
        //super.onBackPressed()
        showCreateListDialog()
    }
}
