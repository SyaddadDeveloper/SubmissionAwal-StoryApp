package com.example.submissionstoryapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissionstoryapp.data.repository.UserRepository
import com.example.submissionstoryapp.data.response.SignupResponse
import kotlinx.coroutines.launch
import com.example.submissionstoryapp.data.Result

class SignupViewModel(private val storyRepository: UserRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<Result<SignupResponse>>()
    val registrationResult: LiveData<Result<SignupResponse>> get() = _registrationResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _registrationResult.value = Result.Loading
                val result = storyRepository.signup(name, email, password)
                _registrationResult.postValue(result)
            } catch (e: Exception) {
                _registrationResult.postValue(Result.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }
}