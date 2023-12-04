package com.example.submissionstoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submissionstoryapp.data.repository.UserRepository
import com.example.submissionstoryapp.data.pref.UserModel
import com.example.submissionstoryapp.data.response.StoryResponse
import kotlinx.coroutines.launch
import com.example.submissionstoryapp.data.Result

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val listStory = MutableLiveData<Result<StoryResponse>>()
    val dataStory: LiveData<Result<StoryResponse>> = listStory

    init {
        getStories()
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories() {
        viewModelScope.launch {
            val storyResponse = repository.getStories()
            storyResponse.asFlow().collect {
                listStory.value = it
            }
        }
    }
}