package com.pauenrech.regalonavidadpauenrech.fragments

import android.content.Context
import android.gesture.Gesture
import android.gesture.GestureOverlayView
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pauenrech.regalonavidadpauenrech.GameActivity

import com.pauenrech.regalonavidadpauenrech.R
import kotlinx.android.synthetic.main.fragment_question.view.*

class QuestionFragment : Fragment(),
    GestureOverlayView.OnGesturePerformedListener{

    companion object {
        fun newInstance(id: String, question: String, isTrue: Boolean, position: Int): QuestionFragment {
            val fragment = QuestionFragment()
            val args = Bundle()

            args.putString("answerId",id)
            args.putString("question",question)
            args.putBoolean("isTrue",isTrue)
            args.putInt("position",position)

            fragment.setArguments(args)

            return fragment
        }
    }

    var activityCallback: QuestionFragment.gestureDetectorListener? = null

    interface gestureDetectorListener{
        fun onGestureDetected(isTrue: Boolean)
    }

    var rootView: View? = null

    var preguntaCorrecta: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preguntaCorrecta = arguments?.getBoolean("isTrue")

        rootView = inflater.inflate(R.layout.fragment_question, container, false)
        rootView!!.questionFragmentPregunta.text = arguments?.getString("question")

        rootView!!.gestureOverlayQuestion.addOnGesturePerformedListener(this)

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityCallback = context as gestureDetectorListener
        } catch (error: ClassCastException){
            throw ClassCastException("Necesitamos que algún objecto implemente el clickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        activityCallback = null
    }

    override fun onGesturePerformed(overlay: GestureOverlayView?, gesture: Gesture?) {
        val prediction = GameActivity.gestureLibrary?.recognize(gesture)
        prediction?.let {
            if(it.size > 0 && it[0].score > 2.0){
                if (it[0].name == "tick"){
                   activityCallback!!.onGestureDetected(preguntaCorrecta == true)
                }
                if (it[0].name == "inverse_tick"){
                    activityCallback!!.onGestureDetected(preguntaCorrecta == false)
                }

                rootView?.gestureOverlayQuestion?.visibility = View.GONE
                rootView?.gestureOverlayQuestion?.removeAllOnGesturePerformedListeners()
            }
        }
    }
}
