package rja.justwatchfortv.search

import android.os.Bundle
import android.support.v17.leanback.app.SearchSupportFragment
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.ListRow
import android.support.v17.leanback.widget.ListRowPresenter
import android.support.v17.leanback.widget.ObjectAdapter
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rja.justwatchfortv.content.ContentItemViewClickedListener
import rja.justwatchfortv.content.ContentPresenter
import rja.justwatchfortv.JustWatchAdapter
import rja.justwatchfortv.R

class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("SearchFragment", "onCreate called")
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("SearchFragment", "onActivityCreated is called")
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        setSearchResultProvider(this)
        setOnItemViewClickedListener(ContentItemViewClickedListener(activity))
        //setSearchQuery("Guardians of the galaxy", true)
        title = resources.getString(R.string.browse_title)
    }

    override fun onQueryTextChange(newQuery: String?): Boolean {
        if (newQuery!=null && newQuery.isNotEmpty()) {
            doAsync {
                val result = JustWatchAdapter().searchAhead(newQuery)
                uiThread {
                    rowsAdapter.clear()
                    if (result.isNotEmpty()) {
                        val rowAdapter = ListRow(ArrayObjectAdapter(ContentPresenter()))
                        for (content in result) {
                            (rowAdapter.adapter as ArrayObjectAdapter).add(content)
                        }
                        rowsAdapter.add(rowAdapter)
                    }
                }
            }
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null && query.isNotEmpty()) {
            doAsync {
                val result = JustWatchAdapter().search(query)
                uiThread {
                    rowsAdapter.clear()
                    if (result.isNotEmpty()) {
                        val rowAdapter = ListRow(ArrayObjectAdapter(ContentPresenter()))
                        for (content in result) {
                            (rowAdapter.adapter as ArrayObjectAdapter).add(content)
                        }
                        rowsAdapter.add(rowAdapter)
                    }
                }
            }
        }
        return true
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }


}
