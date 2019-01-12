package com.pauenrech.regalonavidadpauenrech

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_selection.*
import android.support.v4.view.ViewPager
import android.util.Log
import com.pauenrech.regalonavidadpauenrech.adapters.selectionViewPager
import com.pauenrech.regalonavidadpauenrech.fragments.ThemeSelectionFragment


private lateinit var mPager: ViewPager

class SelectionActivity : AppCompatActivity(), ThemeSelectionFragment.clickListener {

    val userDataReference = HomeActivity.userData
    val temasDataReference = HomeActivity.temasData.lista.temas
    var fragmentsList: MutableList<ThemeSelectionFragment> = mutableListOf()
    var pagerAdapter: selectionViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        selection_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        selection_toolbar.setTitle(R.string.selection_title)

        selection_toolbar.setNavigationOnClickListener { onBackPressed() }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager)

        window.navigationBarColor = getColor(R.color.colorGradientEnd)

        fillPageViewer()


    }

    override fun onBackPressed() {
        if (mPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            mPager.currentItem = mPager.currentItem - 1
        }
    }

    fun fillPageViewer(){
        userDataReference.user.temas[userDataReference.user.dificultad].forEach {userTheme->
            val theme = temasDataReference.filter { it.id == userTheme.id}[0]
            fragmentsList.add(ThemeSelectionFragment.newInstance(userTheme.name,userTheme.score,theme.colorStart,theme.colorEnd,theme.id))
        }
        // The pager adapter, which provides the pages to the view pager widget.
        pagerAdapter = selectionViewPager(supportFragmentManager,fragmentsList)
        mPager.adapter = pagerAdapter
    }

    override fun onCardClicked(title: String, startColor: String, endColor: String, temaId: String) {
        val intent = Intent(this,GameActivity::class.java)
        intent.putExtra("title",title)
        intent.putExtra("temaID",temaId)
        intent.putExtra("startColor",startColor)
        intent.putExtra("endColor",endColor)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
    }
}
