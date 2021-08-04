package com.sriyank.javatokotlindemo.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.sriyank.javatokotlindemo.R
import com.sriyank.javatokotlindemo.adapters.DisplayAdapter
import com.sriyank.javatokotlindemo.app.Constants
import com.sriyank.javatokotlindemo.app.showErrorMessage
import com.sriyank.javatokotlindemo.app.toast
import com.sriyank.javatokotlindemo.models.SearchResponse
import com.sriyank.javatokotlindemo.retrofit.GithubAPIService
import com.sriyank.javatokotlindemo.retrofit.RetrofitClient
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_display.*
import kotlinx.android.synthetic.main.header.view.*
import okhttp3.Call
import retrofit2.Response
import javax.security.auth.callback.Callback
import com.sriyank.javatokotlindemo.models.Repository as Repository

class DisplayActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //lateinit mean you can initiate variable within any method
    //use latenint for mutable variable
    //initialize lateinit variable before using it
      lateinit var displayAdapter : DisplayAdapter
     private var browsedRepositories: List<Repository?> = mutableListOf()

    //lazy must initialize once the shared among all method

    private val githubAPIService: GithubAPIService by lazy {
        RetrofitClient.githubAPIService
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Showing Browsed Results"

        setAppUsername()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        navigationView.setNavigationItemSelectedListener(this)

        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val intent = intent
        if (intent.getIntExtra(Constants.KEY_QUERY_TYPE, -1) == Constants.SEARCH_BY_REPO) {
            val queryRepo = intent.getStringExtra(Constants.KEY_REPO_SEARCH)
            val repoLanguage = intent.getStringExtra(Constants.KEY_LANGUAGE)
//            if (queryRepo != null) {
//                if (repoLanguage != null) {
//                    fetchRepositories(queryRepo, repoLanguage)
//                }
//            }
//        } else {
//            val githubUser = intent.getStringExtra(Constants.KEY_GITHUB_USER)
//            if (githubUser != null) {
//                fetchUserRepositories(githubUser)
//            }
        }
    }

    private fun setAppUsername() {

        val sp = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val personName = sp.getString(Constants.KEY_PERSON_NAME, "User")

        val headerView = navigationView.getHeaderView(0)
        headerView.txvName.text = personName
    }

//    private fun fetchUserRepositories(githubUser: String) {
//
//        with(githubAPIService) {
//            searchRepositoriesByUser(githubUser)!!.enqueue(object : Callback<List<Repository>>, retrofit2.Callback<List<Repository?>?> {
//
//             fun onResponse(call: Call<List<Repository>>?, response: Response<List<Repository>>) =
//                     if (response.isSuccessful) {
//                         Log.i(TAG, "posts loaded from API " + response)
//
//                         response.body()?.let {
//                             browsedRepositories = it
//                         }
//
//                         if (browsedRepositories.isNotEmpty()) {
//                             setupRecyclerView(browsedRepositories)
//                         } else {
//                             toast("No Items Found")
//                         }
//
//                     } else {
//                         Log.i(TAG, "Error " + response)
//                         showErrorMessage(response.errorBody()!!)
//                     }
//
//            override fun onFailure(call: Call<List<Repository>>?, t: Throwable) {
//                toast(t.message ?: "Error Fetching Results")
//            }
//
//            override fun onResponse(call: retrofit2.Call<List<Repository?>?>, response: Response<List<Repository?>?>) {
//                TODO("Not yet implemented")
//            }
//        })
//        }
//    }
//
//    private fun fetchRepositories(queryRepo: String, repoLanguage: String) {
//        var queryRepo = queryRepo
//
//        val query = HashMap<String, String>()
//
//        if (repoLanguage.isNotEmpty())
//            queryRepo += " language:" + repoLanguage
//        query.put("q", queryRepo)
//
//        githubAPIService.searchRepositories(query)?.enqueue(object : Callback<SearchResponse> {
//            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "posts loaded from API " + response)
//
//                    response.body()?.items?.let {
//                        browsedRepositories = it
//                    }
//
//                    if (browsedRepositories.isNotEmpty())
//                        setupRecyclerView(browsedRepositories)
//                    else
//                        toast("No Items Found")
//
//                } else {
//                    Log.i(TAG, "error " + response)
//                    showErrorMessage(response.errorBody()!!)
//                }
//            }
//
////            fun onFailure(call: Call<SearchResponse>, t: Throwable) {
////                toast(t.toString())
////            }
//        })
//    }
//
   private fun setupRecyclerView(items: List<Repository?>) {
        displayAdapter = DisplayAdapter(this, items)
        recyclerView.adapter = displayAdapter
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        menuItem.isChecked = true

        when (menuItem.itemId) {

            R.id.item_bookmark -> { consumeMenuEvent({ showBookmarks() }, "Showing Bookmarks") }
            R.id.item_browsed_results -> { consumeMenuEvent({ showBrowsedResults() }, "Showing Browsed Results") }
        }

        return true
    }

    private inline fun consumeMenuEvent(myFunc: () -> Unit, title: String) {
        myFunc()
        closeDrawer()
        supportActionBar!!.title = title
    }

    private fun showBrowsedResults() {
        displayAdapter.swap(browsedRepositories)
    }

    private fun showBookmarks() {

        val realm = Realm.getDefaultInstance()

        realm.executeTransaction { realm ->
            val bookmarkedRepoList = realm.where(Repository::class.java).findAll()
            displayAdapter.swap(bookmarkedRepoList)
        }
    }

    private fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer()
        else {
            super.onBackPressed()
        }
    }

    companion object {

        private val TAG = DisplayActivity::class.java.simpleName
    }
}



