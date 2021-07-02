package com.indev.geeknewsapps.ui.category.movie

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.indev.geeknewsapps.R
import com.indev.geeknewsapps.ui._model.ModelData
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_category_event.carouselView
import kotlinx.android.synthetic.main.activity_category_movie.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class CategoryMovieActivity : AppCompatActivity() {

    private var verticalList: ArrayList<ModelData> = arrayListOf()

    val crImages = intArrayOf(
        R.drawable.img_5,
        R.drawable.img_1,
        R.drawable.dummy_poster,
        R.drawable.img_2
    )

    val imagesListener = ImageListener {
            position, imageView ->
        imageView.setImageResource(crImages[position])
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_movie)

        toolbar_category.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_black, null)
        toolbar_category.setNavigationOnClickListener {
            onBackPressed()
        }

        carouselView.pageCount = crImages.size
        carouselView.setImageListener(imagesListener)

        rc_MovieVertical.setHasFixedSize(true)
        verticalList.addAll(CategoryMovieData.listData)
        showRecyclerVertical()
    }

    private fun showRecyclerVertical() {
        rc_MovieVertical.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val movieVerticalAdapter = MovieVerticalAdapter(verticalList)
        rc_MovieVertical.adapter = movieVerticalAdapter
    }
}