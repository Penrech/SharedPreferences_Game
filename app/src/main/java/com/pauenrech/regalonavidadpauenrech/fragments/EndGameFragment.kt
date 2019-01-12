package com.pauenrech.regalonavidadpauenrech.fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pauenrech.regalonavidadpauenrech.R
import kotlinx.android.synthetic.main.fragment_end_game.view.*


class EndGameFragment : Fragment() {

    var activityCallback: EndGameFragment.clickListener? = null

    interface clickListener{
        fun onEndGameButtonClick()
    }

    companion object {
        var endFragmentView: View? = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_end_game, container, false)
        endFragmentView = rootView
        rootView.endGameButton.setOnClickListener {
            activityCallback!!.onEndGameButtonClick()
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
