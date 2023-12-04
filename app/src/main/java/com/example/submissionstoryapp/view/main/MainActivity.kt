package com.example.submissionstoryapp.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityMainBinding
import com.example.submissionstoryapp.data.ViewModelFactory
import com.example.submissionstoryapp.view.welcome.WelcomeActivity
import com.example.submissionstoryapp.data.Result

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpAction()

        viewModel.getSession().observe(this) {

        }

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_actionbar)

        itemDecoration()

        viewModel.getSession().observe(this) { user ->
            Log.d("token", "onCreate: ${user.token}")
            Log.d("user", "onCreate: ${user.isLogin}")
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            viewModel.dataStory.observe(this) { story ->
                if (story != null) {
                    when (story) {
                        is Result.Loading -> {
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                            showLoading(true)
                        }

                        is Result.Success -> {
                            val storyData = story.data.listStory
                            val storyAdapter = StoryAdapter()
                            storyAdapter.submitList(storyData)
                            binding.rvStory.adapter = storyAdapter
                            showLoading(false)
                        }

                        is Result.Error -> {
                            showLoading(false)
                        }
                    }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
    }

    //persiapan menambahkan story
    private fun setUpAction() {
    }

    private fun itemDecoration() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressBarMain.visibility = View.VISIBLE
            } else {
                progressBarMain.visibility = View.GONE
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu1 -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.menu2 -> {
                viewModel.logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}