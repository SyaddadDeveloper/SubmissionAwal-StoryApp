package com.example.submissionstoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submissionstoryapp.data.Result
import com.example.submissionstoryapp.data.api.ApiConfig
import com.example.submissionstoryapp.data.api.ApiService
import com.example.submissionstoryapp.data.db.StoryDataBase
import com.example.submissionstoryapp.data.paging.StoryRemoteMediator
import com.example.submissionstoryapp.data.pref.UserModel
import com.example.submissionstoryapp.data.pref.UserPreference
import com.example.submissionstoryapp.data.response.ErrorResponse
import com.example.submissionstoryapp.data.response.ListStoryItem
import com.example.submissionstoryapp.data.response.LoginResponse
import com.example.submissionstoryapp.data.response.SignupResponse
import com.example.submissionstoryapp.data.response.UploadResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val dataBase: StoryDataBase,
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {

    private suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun signup(name: String, email: String, password: String): Result<SignupResponse> {
        Result.Loading
        return try {
            val response = apiService.register(name, email, password)
            if (response.error == true) {
                Result.Error(response.message ?: "Unknown error")
            } else {
                Result.Success(response)
            }
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Result.Error(errorMessage.toString())
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        Result.Loading
        return try {
            val response = apiService.login(email, password)

            if (response.error) {
                Result.Error(response.message)
            } else {
                val session = UserModel(
                    name = response.loginResult.name,
                    email = email,
                    token = response.loginResult.token,
                    isLogin = true
                )
                saveSession(session)
                ApiConfig.token = response.loginResult.token
                Result.Success(response)
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Result.Error(errorMessage.toString())
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(dataBase, apiService),
            pagingSourceFactory = {
                dataBase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoriesWithLocation(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            emit(Result.Success(response.listStory))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Result.Error(errorMessage.toString())
        }
    }

    fun uploadStories(imageFile: File, description: String,lat: Double?, lon: Double?) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val requestLat = lat?.toString()?.toRequestBody()
        val requestLon = lon?.toString()?.toRequestBody()
        try {
            val successResponse = apiService.uploadImage(multipartBody, requestBody, requestLat, requestLon)
            if (successResponse.error) {
                emit(Result.Error(successResponse.message))
            } else {
                emit(Result.Success(successResponse))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(Result.Error("Error: $errorResponse"))
        }
    }

    companion object {
        fun getInstance(
            dataBase: StoryDataBase,
            apiService: ApiService,
            userPreference: UserPreference,
        ): UserRepository = UserRepository(dataBase, apiService, userPreference)
    }
}