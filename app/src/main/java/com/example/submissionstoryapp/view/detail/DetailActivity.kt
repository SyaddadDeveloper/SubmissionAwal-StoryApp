package com.example.submissionstoryapp.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra(ID) ?: ""
        name = intent.getStringExtra(NAME) ?: ""
        description = intent.getStringExtra(DESCRIPTION) ?: ""
        picture = intent.getStringExtra(PICTURE) ?: ""

        binding.tvDetailName.text = name
        binding.tvDetailName.text = description

        Glide.with(this).load(picture).into(binding.ivDetailStory)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_actionbar)
    }

    companion object {
        const val ID = "ID"
        const val NAME = "NAME"
        const val DESCRIPTION = "DESCRIPTION"
        const val PICTURE = "PICTURE"

        var id: String = ""
        var name: String = ""
        var description: String? = null
        var picture: String? = null
    }
}