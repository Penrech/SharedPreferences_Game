package com.pauenrech.regalonavidadpauenrech.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pauenrech.regalonavidadpauenrech.R
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.fragment_game_controller.*


class GameControllerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_game_controller, container, false)
        return rootView
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onDetach() {
        super.onDetach()
    }




}
