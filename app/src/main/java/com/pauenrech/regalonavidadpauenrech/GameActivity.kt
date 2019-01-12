package com.pauenrech.regalonavidadpauenrech

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.fragment_game_controller.*
import org.w3c.dom.Text
import java.awt.font.TextAttribute
import java.sql.Time
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.LayoutInflater
import com.pauenrech.regalonavidadpauenrech.adapters.selectionViewPager
import com.pauenrech.regalonavidadpauenrech.fragments.*
import com.pauenrech.regalonavidadpauenrech.model.User
import com.pauenrech.regalonavidadpauenrech.tools.CustomViewPager
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_end_game.*
import java.lang.Exception


private lateinit var mPager: CustomViewPager

class GameActivity : AppCompatActivity(),
    StartGameFragment.clickListener,
    EndGameFragment.clickListener,
    QuestionFragment.clickListener{

    enum class GameState{
        PreInit, Running, Paused, Finish
    }

    enum class TimerState{
        Stopped, Paused, Running
    }

    var textTransition : Transition? = null
    var controlFragmentReference: GameControllerFragment? = null
    var endGameFragmentReference: EndGameFragment? = null
    var timer : CountDownTimer? = null
    var timerState = TimerState.Stopped
    var gameState = GameState.PreInit
    var oldGameState = GameState.PreInit
    var segundosTimer: Long = 0
    var segundosQueQuedan: Long = 5
    var pointsGained: Int = 0
    var actualPosition: Int = 0
    var fragmentsList: MutableList<Fragment> = mutableListOf()
    var pagerAdapter: selectionViewPager? = null
    var userReference = HomeActivity.userData
    var temasReference = HomeActivity.temasData.lista.temas
    var preguntasReference = HomeActivity.preguntasData.listaPreguntasTotal.totalPreguntas
    var temaID : String? = null
    var startFragment: Fragment? = null
    var endFragment: Fragment? = null
    var temaReference: User.ThemeScore? = null
    var valorPregunta: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        controlFragmentReference = gameControllerFragment as GameControllerFragment


        temaID = intent.getStringExtra("temaID")
        val title = intent.getStringExtra("title")
        val startColorString = intent.getStringExtra("startColor")
        val endColorString = intent.getStringExtra("endColor")
        val startColor = Color.parseColor(startColorString)
        val endColor = Color.parseColor(endColorString)
        val colors2 = intArrayOf(startColor, endColor)
        val backgroundGradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors2)
        backgroundGradientDrawable.cornerRadius = 0f
        controlFragmentReference?.gameControllerRatingBar?.rating = (pointsGained / 2f)

        updateCountdownUI()


        rootGame.background = backgroundGradientDrawable
        window.navigationBarColor = endColor

        mPager = findViewById(R.id.gamePager)
        mPager.setPagingEnabled(false)

        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                Log.i("ANSWER","Pagina seleccionada: $position")

                if (position > 0 && position < 11){
                    hideCorrectionLabel()
                }
                if (position <= 10){
                    actualPosition = position
                    controlFragmentReference?.gameControllerPreguntas?.text = "$position/10"
                }
                if (position > 10){
                    actualPosition = position
                    timerState = TimerState.Stopped
                    gameState = GameState.Finish
                    lastPageLabel()
                }
            }

        })

        textTransition = TransitionInflater.from(this)
            .inflateTransition(R.transition.correction_text_appear)

        game_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        game_toolbar.setTitle(title)

        game_toolbar.setNavigationOnClickListener { onBackPressed() }

        fillPageViewer()


    }

    fun fillPageViewer(){
        /*userDataReference.user.temas[userDataReference.user.dificultad].forEach {userTheme->
            val theme = temasDataReference.filter { it.id == userTheme.id}[0]
            fragmentsList.add(ThemeSelectionFragment.newInstance(userTheme.name,userTheme.score,theme.colorStart,theme.colorEnd,theme.id))
        }*/
        startFragment = StartGameFragment()
        fragmentsList.add(startFragment!!)
        val actualTemaRef = preguntasReference[userReference.user.dificultad].temas.filter { it.id == temaID }
        val listaPreguntasAleatorias = actualTemaRef[0].getRandomQuestions()
        listaPreguntasAleatorias.forEachIndexed { index, pregunta ->
            if (index == 0){
                valorPregunta = pregunta.puntuacion
            }
            val randomAnswer = pregunta.getRandomAnswer()
            fragmentsList.add(QuestionFragment.newInstance(pregunta.id!!,randomAnswer.first,randomAnswer.second,index + 1))
        }
        endFragment = EndGameFragment()
        fragmentsList.add(endFragment!!)
        // The pager adapter, which provides the pages to the view pager widget.
        pagerAdapter = selectionViewPager(supportFragmentManager,fragmentsList)
        mPager.adapter = pagerAdapter
    }

    fun startTimer(){
        timerState = TimerState.Running
        gameState = GameState.Running

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

    fun updateStarRatingUI(){
        pointsGained++
        controlFragmentReference?.gameControllerRatingBar?.rating = (pointsGained / 2f)
    }

    fun onTimerFinished(){
        timerState = TimerState.Paused
        if (actualPosition == 0){
            showCorrectionLabel(R.string.counter_start_game)
        }
        else{
            showCorrectionLabel(R.string.counter_timeout)
        }

        wait1secondAndPass()
    }

    fun onUserAswer(isUserRight: Boolean){

        if (timerState == TimerState.Running){
            timerState = TimerState.Paused
            timer?.cancel()
            if (isUserRight) {
                showCorrectionLabel(R.string.counter_correct)
                updateStarRatingUI()
            }
            else
                showCorrectionLabel(R.string.counter_incorrect)
            wait1secondAndPass()
        }
    }

    fun showCorrectionLabel(message: Int){

        TransitionManager.beginDelayedTransition(rootGame,textTransition)
        controlFragmentReference?.gameControllerCorrection?.text = getString(message)
        controlFragmentReference?.gameControllerCorrection?.visibility = View.VISIBLE
        val params = controlFragmentReference?.gameControllerTimer?.getLayoutParams() as ConstraintLayout.LayoutParams
        params.horizontalBias = 0f // here is one modification for example. modify anything else you want :)
        controlFragmentReference?.gameControllerTimer?.setLayoutParams(params) // request the view to use the new modified params


    }

    fun hideCorrectionLabel(){
        TransitionManager.beginDelayedTransition(rootGame,textTransition)
        controlFragmentReference?.gameControllerCorrection?.visibility = View.GONE
        val params = controlFragmentReference?.gameControllerTimer?.getLayoutParams() as ConstraintLayout.LayoutParams
        params.horizontalBias = 0.5f // here is one modification for example. modify anything else you want :)
        controlFragmentReference?.gameControllerTimer?.setLayoutParams(params) // request the view to use the new modified params
        segundosQueQuedan = 5
        updateCountdownUI()
        startTimer()
    }

    fun lastPageLabel(){
        TransitionManager.beginDelayedTransition(rootGame,textTransition)
        endFragment?.endGameCardRatingBar?.rating = (pointsGained / 2f)
        endFragment?.endGameCorrectAnswers?.text = "$pointsGained/10"
        val fragmentRef = fragmentsList[actualPosition]
        fragmentRef.endGameCardRatingBar.rating = (pointsGained / 2f)
        controlFragmentReference?.gameControllerCorrection?.text = "Gameover"
        controlFragmentReference?.gameControllerCorrection?.visibility = View.VISIBLE
        val params = controlFragmentReference?.gameControllerTimer?.getLayoutParams() as ConstraintLayout.LayoutParams
        params.horizontalBias = 0f // here is one modification for example. modify anything else you want :)
        controlFragmentReference?.gameControllerTimer?.setLayoutParams(params) // request the view to use the new modified params
        setUserPoints(fragmentRef)

    }

    fun setUserPoints(fragment: Fragment){
        val oldScore = userReference.user.getTemaScore(temaID!!)
       if(oldScore < pointsGained){
           TransitionManager.beginDelayedTransition(rootGame,textTransition)
           fragment.endGameNewRecordGroup.visibility = View.VISIBLE

           val diff = pointsGained - oldScore
           val points = diff * valorPregunta!!
           fragment.endGamePointsObtined.text = "+$points"
           userReference.actualizarPuntuacionTema(temaID!!,pointsGained)
           userReference.changePuntuacion(userReference.user.puntuacion + points)


       }

    }

    fun wait1secondAndPass(){
        oneSecondAsyncTask().execute()

    }

    fun createDialog(){

        Log.i("ASYNCTASK","Status: ${oneSecondAsyncTask().status}")

        pauseGameAndTimer()
        Log.i("ASYNCTASK","estado timer: $timerState , estado juego $gameState")
        if (oneSecondAsyncTask().status == AsyncTask.Status.RUNNING || oneSecondAsyncTask().status == AsyncTask.Status.RUNNING){
            oneSecondAsyncTask().cancel(true)
        }


        val dialogLista = AlertDialog.Builder(this).create()

        val customView = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog,null)
        customView.customDialogTitle.text = getString(R.string.custom_dialog_leave_game)
        customView.customDialogMessage.text = getString(R.string.custom_dialog_leave_game_message)

        customView.customDialogPrimaryButton.text = getString(R.string.dialog_exit_button_text)
        customView.customDialogSecundaryButton.text = getString(R.string.dialog_cancel_button_text)
        customView.customDialogPrimaryButton.setOnClickListener {
            finish()
            dialogLista.dismiss()
        }
        customView.customDialogSecundaryButton.setOnClickListener {
            resumeGameAndTimer()
            dialogLista.dismiss()

        }
        dialogLista.setView(customView)
        dialogLista.window.setBackgroundDrawable(getDrawable(android.R.color.transparent))
        dialogLista.show()
    }

    fun pauseGameAndTimer(){

        if (timerState == TimerState.Running){
            timer?.cancel()
            timerState = TimerState.Paused
        }
        if (gameState != GameState.PreInit){
            gameState = GameState.Paused
        }
    }

    fun resumeGameAndTimer(){
        if (timerState == TimerState.Paused && gameState == GameState.Paused){
            segundosQueQuedan = 0
            updateCountdownUI()
            startTimer()
        }
        else{
            if (gameState != GameState.PreInit)
                startTimer()
        }


    }

    override fun questionCardClick(isTrue: Boolean, position: Int) {
        if (gameState == GameState.Running){
        onUserAswer(isTrue == true)
        }

    }

    override fun onEndGameButtonClick() {
        finish()
    }

    override fun onStartGameButtonClick() {
        if (gameState == GameState.PreInit){
            gameState = GameState.Running
            segundosQueQuedan = 5
            startTimer()
        }
    }


    override fun onPause() {
        pauseGameAndTimer()

        super.onPause()
    }

    override fun onResume() {
        resumeGameAndTimer()
        super.onResume()
    }

    override fun onBackPressed() {
        if (gameState != GameState.Finish)
            createDialog()
        else
            super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }

    inner class oneSecondAsyncTask: AsyncTask<Boolean,Int,Boolean>(){
        override fun doInBackground(vararg params: Boolean?): Boolean? {
            try {
                Log.i("ASYNCTASK","Entro aquii")
                Thread.sleep(1000)
            }
            catch (e: Exception){
                e.printStackTrace()
            }
            return true
        }

        override fun onProgressUpdate(vararg values: Int?) {
            Log.i("Progress","Progres ${values[0]}")
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Boolean?) {
            Log.i("ASYNCTASK","estado juego dentro async: $gameState")
            if (gameState != GameState.Paused){
                mPager.currentItem = actualPosition + 1
                gameState = GameState.Running
                timerState = TimerState.Running
            }
            super.onPostExecute(result)
        }

        override fun onCancelled(result: Boolean?) {
            super.onCancelled(result)
        }

        override fun onCancelled() {
            super.onCancelled()
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }


}
