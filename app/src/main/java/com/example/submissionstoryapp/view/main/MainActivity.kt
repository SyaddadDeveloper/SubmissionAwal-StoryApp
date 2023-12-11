package com.example.submissionstoryapp.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityMainBinding
import com.example.submissionstoryapp.data.ViewModelFactory
import com.example.submissionstoryapp.view.welcome.WelcomeActivity
import com.example.submissionstoryapp.data.adapter.LoadingStateAdapter
import com.example.submissionstoryapp.view.map.MapsActivity
import com.example.submissionstoryapp.view.story.StoryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var storyAdapter = StoryAdapter()
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAction()
        storyAdapter = StoryAdapter()

        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_actionbar)
        }
        itemDecoration()

        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            })

        viewModel.getSession().observe(this) { user ->
            Log.d("token", "onCreate: ${user.token}")
            Log.d("user", "onCreate: ${user.isLogin}")
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        viewModel.getStoryPagingData().observe(this) { story ->
            storyAdapter.submitData(lifecycle, story)
        }
    }

    override fun onResume() {
        super.onResume()
        storyAdapter.refresh()
    }

    private fun setUpAction() {
        binding.fabAddStory.setOnClickListener {
            Intent(this, StoryActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun itemDecoration() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        super.onBackPressedDispatcher
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
                startActivity(Intent(this, MapsActivity::class.java))
            }

            R.id.menu3 -> {
                viewModel.logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}