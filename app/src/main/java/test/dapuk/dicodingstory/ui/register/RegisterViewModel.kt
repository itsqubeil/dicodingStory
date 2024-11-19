package test.dapuk.dicodingstory.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import test.dapuk.dicodingstory.data.response.StoryResponse
import test.dapuk.dicodingstory.data.retrofit.ApiConfig

class RegisterViewModel : ViewModel() {
    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerErr = MutableLiveData<String>()
    val registerErr: LiveData<String> = _registerErr

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val apiResponse = ApiConfig.getApiService()
            try {
                val response = apiResponse.createUser(name, email, password)
                Log.d("pesan", "${response.message}")
                _registerSuccess.value = true
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
                Log.e("Login Error", "Message: ${errorResponse.message}")
                _registerErr.value = errorResponse.message
            } catch (e: Exception) {
                Log.e("gagal", "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}