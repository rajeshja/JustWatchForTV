package rja.justwatchfortv.search

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import rja.justwatchfortv.R

class SearchActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SearchActivity", "Started SearchActivity")
        setContentView(R.layout.search_activity_input)
    }
}