package com.example.submissionstoryapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionstoryapp.data.repository.UserRepository
import com.example.submissionstoryapp.data.pref.UserModel
import kotlinx.coroutines.launch
import com.example.submissionstoryapp.data.response.ListStoryItem

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getStoryPagingData(): LiveData<PagingData<ListStoryItem>> {
        return repository.getStories().cachedIn(viewModelScope)
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error during logout", e)
            }
        }
    }
}