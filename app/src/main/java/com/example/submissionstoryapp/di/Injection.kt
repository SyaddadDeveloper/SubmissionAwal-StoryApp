package com.example.submissionstoryapp.di

import android.content.Context
import com.example.submissionstoryapp.data.repository.UserRepository
import com.example.submissionstoryapp.data.api.ApiConfig
import com.example.submissionstoryapp.data.db.StoryDataBase
import com.example.submissionstoryapp.data.pref.UserPreference
import com.example.submissionstoryapp.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val dataBase = StoryDataBase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(dataBase, apiService, pref)
    }
}