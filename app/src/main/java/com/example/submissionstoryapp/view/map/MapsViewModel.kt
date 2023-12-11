package com.example.submissionstoryapp.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.Result
import com.example.submissionstoryapp.data.repository.UserRepository
import com.example.submissionstoryapp.data.response.ListStoryItem

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getStoriesWithLocation(): LiveData<Result<List<ListStoryItem>>> {
        return repository.getStoriesWithLocation()
    }
}