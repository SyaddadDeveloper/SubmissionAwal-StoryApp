package com.example.submissionstoryapp.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
        createdAt = intent.getStringExtra(CREATED_AT) ?: ""

        binding.tvDetailName.text = name
        binding.tvDetailDescription.text = description
        binding.tvCreatAt.text = createdAt

        Glide.with(this).load(picture)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivDetailStory)

        ViewCompat.setTransitionName(binding.tvDetailName, "transition_tv_item_name")

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_actionbar)
    }

    companion object {
        const val ID = "ID"
        const val NAME = "NAME"
        const val DESCRIPTION = "DESCRIPTION"
        const val PICTURE = "PICTURE"
        const val CREATED_AT = "createdAt"


        var id: String = ""
        var name: String = ""
        var description: String? = null
        var picture: String? = null
        var createdAt: String? = null
    }
}