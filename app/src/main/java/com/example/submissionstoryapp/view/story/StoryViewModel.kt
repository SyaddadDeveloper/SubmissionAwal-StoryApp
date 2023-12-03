package com.example.submissionstoryapp.view.story


import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.repository.UserRepository
import java.io.File

class StoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadStories(file: File, description: String) = repository.uploadStories(file, description)
}