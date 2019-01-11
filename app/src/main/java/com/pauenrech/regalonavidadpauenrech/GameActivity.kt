package com.pauenrech.regalonavidadpauenrech

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import com.pauenrech.regalonavidadpauenrech.fragments.GameControllerFragment
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.fragment_game_controller.*
import org.w3c.dom.Text
import java.awt.font.TextAttribute
import java.sql.Time
import android.support.constraint.ConstraintLayout
import android.transition.Transition
import android.transition.TransitionInflater


class GameActivity : AppCompatActivity(){

    enum class TimerState{
        Stopped, Paused, Running
    }

    var textTransition : Transition? = null
    var controlFragmentReference: GameControllerFragment? = null
    var timer : CountDownTimer? = null
    var timerState = TimerState.Stopped
    var segundosTimer: Long = 0
    var segundosQueQuedan: Long = 5
    var pointsGained: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        controlFragmentReference = gameControllerFragment as GameControllerFragment

        val title = intent.getStringExtra("title")
        val startColorString = intent.getStringExtra("startColor")
        val endColorString = intent.getStringExtra("endColor")
        val startColor = Color.parseColor(startColorString)
        val endColor = Color.parseColor(endColorString)
        val colors2 = intArrayOf(startColor, endColor)
        val backgroundGradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors2)
        backgroundGradientDrawable.cornerRadius = 0f
        controlFragmentReference?.gameControllerRatingBar?.rating = (pointsGained / 2f)

        rootGame.background = backgroundGradientDrawable
        window.navigationBarColor = endColor

        textTransition = TransitionInflater.from(this)
            .inflateTransition(R.transition.correction_text_appear)

        game_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        game_toolbar.setTitle(title)

        game_toolbar.setNavigationOnClickListener { onBackPressed() }

        startTimer()


    }

    fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(segundosQueQuedan * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                Log.i("TAG","Segundos")
                segundosQueQuedan = millisUntilFinished / 1000
                updateCountdownUI()
            }
            override fun onFinish() = onTimerFinished()
        }.start()
    }

    fun updateCountdownUI(){
        if (segundosQueQuedan < 10){
            controlFragmentReference?.gameControllerTimer?.text = "00:0$segundosQueQuedan"
        }
        else{
            controlFragmentReference?.gameControllerTimer?.text = "00:$segundosQueQuedan"
        }

    }

    fun onTimerFinished(){
       showCorrectionLabel(R.string.counter_timeout)
    }

    fun showCorrectionLabel(message: Int){
        TransitionManager.beginDelayedTransition(rootGame,textTransition)
        controlFragmentReference?.gameControllerCorrection?.text = getString(message)
        controlFragmentReference?.gameControllerCorrection?.visibility = View.VISIBLE
        val params = controlFragmentReference?.gameControllerTimer?.getLayoutParams() as ConstraintLayout.LayoutParams
        params.horizontalBias = 0f // here is one modification for example. modify anything else you want :)
        controlFragmentReference?.gameControllerTimer?.setLayoutParams(params) // request the view to use the new modified params
    }

    override fun onPause() {
        if (timerState == TimerState.Running){
            timer?.cancel()
            timerState = TimerState.Paused
        }
        super.onPause()
    }

    override fun onResume() {
        if (timerState == TimerState.Paused){
            startTimer()
        }
        super.onResume()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }


}
