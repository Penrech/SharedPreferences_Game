package com.pauenrech.regalonavidadpauenrech.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pauenrech.regalonavidadpauenrech.R
import com.pauenrech.regalonavidadpauenrech.model.User
import kotlinx.android.synthetic.main.profile_statics_card.view.*

class profileSpecificListAdapter(var temasPorDificultad: MutableList<User.ThemeScore>)
    : RecyclerView.Adapter<profileSpecificListAdapter.SpecificListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SpecificListViewHolder {
       val celdaPuntuacionEspecifica = LayoutInflater.from(p0.context)
           .inflate(R.layout.profile_statics_card,p0,false)

        return SpecificListViewHolder(celdaPuntuacionEspecifica)
    }

    override fun getItemCount(): Int {
        return temasPorDificultad.size
    }

    override fun onBindViewHolder(p0: SpecificListViewHolder, p1: Int) {
        p0.tema.text = temasPorDificultad[p1].name
        p0.puntuacion.rating = (temasPorDificultad[p1].score / 2f)
    }

    class SpecificListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tema = itemView.profileSpecificTheme
        var puntuacion = itemView.profileRatingBar
    }

    fun changeData(newData: MutableList<User.ThemeScore>){
        temasPorDificultad = newData
        notifyDataSetChanged()
    }
}