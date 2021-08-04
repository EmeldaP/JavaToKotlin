package com.sriyank.javatokotlindemo.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout
import com.sriyank.javatokotlindemo.R
import com.sriyank.javatokotlindemo.activities.MainActivity
import com.sriyank.javatokotlindemo.app.Constants
import com.sriyank.javatokotlindemo.app.isNotEmpty

import kotlinx.android.synthetic.main.activity_main.*;

class MainActivity : AppCompatActivity() {
    //NB: you cant have multiple companion objects
    //for static field and method should be defined it this methods
    companion object{
        private  val  TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

     }

    //to save aap username in sharedPreferences
    fun saveName(view: View) {

        if (etName.isNotEmpty(inputLayoutName)) {

            val personName = etName.text.toString()
            val sp = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString(Constants.KEY_PERSON_NAME, personName)
            editor.apply()

        }
    }
    //to seach repositories on github after passing data to displayActivity
    fun listRepositories(view: View) {
        if (etRepoName.isNotEmpty( inputLayoutRepoName)) {

            val queryRepo = etRepoName.text.toString()
            val repoLanguage = etLanguage.text.toString()

            val intent = Intent(this, DisplayActivity::class.java)
            intent.putExtra(Constants.KEY_QUERY_TYPE, Constants.SEARCH_BY_REPO)
            intent.putExtra(Constants.KEY_REPO_SEARCH, queryRepo)
            intent.putExtra(Constants.KEY_LANGUAGE, repoLanguage)
            startActivity(intent)
        }
    }
    //to seach particular repositories after passing data to displayActivity
    fun listUserRepositories(view: View) {
        if (etGithubUser.isNotEmpty( inputLayoutGithubUser)) {
            val githubUser = etGithubUser.text.toString()
            val intent = Intent(this, DisplayActivity::class.java)
            intent.putExtra(Constants.KEY_QUERY_TYPE, Constants.SEARCH_BY_USER)
            intent.putExtra(Constants.KEY_GITHUB_USER, githubUser)
            startActivity(intent)
        }
    }
}