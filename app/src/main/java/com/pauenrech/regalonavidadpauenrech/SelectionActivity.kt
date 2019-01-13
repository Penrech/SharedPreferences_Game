package com.pauenrech.regalonavidadpauenrech

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_selection.*
import android.support.v4.view.ViewPager
import com.pauenrech.regalonavidadpauenrech.adapters.selectionViewPager
import com.pauenrech.regalonavidadpauenrech.fragments.ThemeSelectionFragment
import kotlinx.android.synthetic.main.fragment_theme_selection.*

class SelectionActivity : AppCompatActivity(), ThemeSelectionFragment.clickListener {

    private lateinit var mPager: ViewPager
    var pagerAdapter: selectionViewPager? = null

    val userDataReference = HomeActivity.userData
    val temasDataReference = HomeActivity.temasData.lista.temas

    var fragmentsList: MutableList<ThemeSelectionFragment> = mutableListOf()
    var themeInFragmentId: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        selection_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        selection_toolbar.setTitle(R.string.selection_title)

        selection_toolbar.setNavigationOnClickListener { onBackPressed() }

        mPager = findViewById(R.id.pager)

        window.navigationBarColor = getColor(R.color.colorGradientEnd)

        fillPageViewer()
    }

    fun fillPageViewer(){
        userDataReference.user.temas[userDataReference.user.dificultad].forEach {userTheme->
            val theme = temasDataReference.filter { it.id == userTheme.id}[0]

            fragmentsList.add(ThemeSelectionFragment.newInstance(userTheme.name,userTheme.score,theme.colorStart,theme.colorEnd,theme.id))
            themeInFragmentId.add(theme.id)
        }

        pagerAdapter = selectionViewPager(supportFragmentManager,fragmentsList)
        mPager.adapter = pagerAdapter
    }

    fun refreshPageViewer(temaId: String){
        val index = themeInFragmentId.indexOf(temaId)
        val score = userDataReference.user.getTemaScore(temaId)

        if (score != -1){
            val fragmentToChange = fragmentsList[index]
            fragmentToChange.selectionCardRatingBar.rating = (score / 2f)
        }
    }

    override fun onCardClicked(title: String, startColor: String, endColor: String, temaId: String) {
        val intent = Intent(this,GameActivity::class.java)
        intent.putExtra("title",title)
        intent.putExtra("temaID",temaId)
        intent.putExtra("startColor",startColor)
        intent.putExtra("endColor",endColor)
        startActivityForResult(intent,101)

        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 101 && resultCode == Activity.RESULT_OK){
            val temaId = data?.getStringExtra("temaID")
            refreshPageViewer(temaId!!)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
