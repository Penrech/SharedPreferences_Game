package com.pauenrech.regalonavidadpauenrech.fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pauenrech.regalonavidadpauenrech.R
import kotlinx.android.synthetic.main.fragment_question.*
import kotlinx.android.synthetic.main.fragment_question.view.*

class QuestionFragment : Fragment() {

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

    var activityCallback: QuestionFragment.clickListener? = null
    interface clickListener{
       fun questionCardClick(isTrue: Boolean, position: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_question, container, false)
        rootView.questionFragmentPregunta.text = arguments?.getString("question")
        rootView.questionCardView.setOnClickListener {
            activityCallback!!.questionCardClick(arguments?.getBoolean("isTrue")!!,arguments?.getInt("position")!!)
        }
        return rootView
    }

    override fun onAttach(context: Context?) {

        super.onAttach(context)
        try {
            activityCallback = context as clickListener
        } catch (error: ClassCastException){
            throw ClassCastException("Necesitamos que algún objecto implemente el clickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        activityCallback = null
    }


}
