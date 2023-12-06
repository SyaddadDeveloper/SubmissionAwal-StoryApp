package com.example.submissionstoryapp.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionstoryapp.data.response.ListStoryItem
import com.example.submissionstoryapp.databinding.ItemStoryBinding
import com.example.submissionstoryapp.view.detail.DetailActivity

class StoryAdapter :
    ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemName: ListStoryItem?) {
            itemName?.let {
                binding.tvItemName.text = itemName.name
                Glide.with(binding.root)
                    .load(itemName.photoUrl)
                    .into(binding.ivItemStory)
                binding.tvItemDescription.text = itemName.description
                binding.root.setOnClickListener {
                    val intentDetail = Intent(binding.root.context, DetailActivity::class.java)
                    intentDetail.putExtra(DetailActivity.ID, itemName.id)
                    intentDetail.putExtra(DetailActivity.NAME, itemName.name)
                    intentDetail.putExtra(DetailActivity.DESCRIPTION, itemName.description)
                    intentDetail.putExtra(DetailActivity.PICTURE, itemName.photoUrl)
                    intentDetail.putExtra(DetailActivity.CREATED_AT, itemName.createdAt)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        binding.root.context as Activity,
                        Pair(binding.ivItemStory as View, "transition_iv_item_story"),
                        Pair(binding.tvItemName as View, "transition_tv_item_name")
                    ).toBundle()
                    binding.root.context.startActivity(intentDetail, options)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
