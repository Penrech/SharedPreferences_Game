package com.pauenrech.regalonavidadpauenrech.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import kotlinx.android.synthetic.main.fragment_theme_selection.*
import java.lang.Exception


class selectionViewPager(fragmentManager: FragmentManager,
                         private var listFragment: List<Fragment>) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getCount(): Int {
        return listFragment.size
    }


}