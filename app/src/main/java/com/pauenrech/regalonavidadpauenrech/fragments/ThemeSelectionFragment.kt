package com.pauenrech.regalonavidadpauenrech.fragments


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pauenrech.regalonavidadpauenrech.GameActivity

import com.pauenrech.regalonavidadpauenrech.R
import kotlinx.android.synthetic.main.fragment_theme_selection.*
import kotlinx.android.synthetic.main.fragment_theme_selection.view.*

class ThemeSelectionFragment : Fragment() {

    companion object {
        fun newInstance(title: String, score: Int, startColor: String, endColor: String, temaId: String): ThemeSelectionFragment {
            val fragment = ThemeSelectionFragment()
            val args = Bundle()
            args.putString("cardTitle", title)
            args.putInt("cardScore",score)
            args.putString("startColor",startColor)
            args.putString("endColor",endColor)
            args.putString("temaId",temaId)
            fragment.setArguments(args)
            return fragment
        }
    }

    var activityCallback: ThemeSelectionFragment.clickListener? = null
   interface clickListener{
       fun onCardClicked(title: String, startColor: String, endColor: String,temaId: String)
   }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_theme_selection, container, false)
        val temaId = arguments?.getString("temaId")
        val cardTitle = arguments?.getString("cardTitle")
        val startColorString = arguments?.getString("startColor")
        val endColorString = arguments?.getString("endColor")
        val startColor = Color.parseColor(startColorString)
        val endColor = Color.parseColor(endColorString)
        rootView.selectionCardTitle.text = cardTitle
        rootView.selectionCardView.setCardBackgroundColor(startColor)
        arguments?.getInt("cardScore").let {
            rootView.selectionCardRatingBar.rating = (it!! / 2f)
        }
        rootView.selectionCardView.setOnClickListener {
            activityCallback!!.onCardClicked(cardTitle!!,startColorString!!,endColorString!!,temaId!!)
            /*val intent = Intent(container?.context,GameActivity::class.java)
            intent.putExtra("title",cardTitle)
            intent.putExtra("startColor",startColorString)
            intent.putExtra("endColor",endColorString)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)*/
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
/*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        var args = arguments
        Log.i("FRAGMENT","${args}")
        if (args != null){
            selectionCardTitle.text = args.getString("cardTitle")
            selectionCardRatingBar.rating = (args.getInt("cardScore") / 2f)
        }
        super.onActivityCreated(savedInstanceState)
    }*/



}
