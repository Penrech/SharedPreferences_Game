package com.pauenrech.regalonavidadpauenrech.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pauenrech.regalonavidadpauenrech.R
import kotlinx.android.synthetic.main.fragment_start_game.view.*

class StartGameFragment : Fragment() {

    var activityCallback: StartGameFragment.clickListener? = null

    interface clickListener{
        fun onStartGameButtonClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_start_game, container, false)

        rootView.startGameButton.setOnClickListener {
            activityCallback!!.onStartGameButtonClick()
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
