package test.dapuk.dicodingstory.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import test.dapuk.dicodingstory.data.repository.StoryRepository
import test.dapuk.dicodingstory.data.response.ListStoryItem
import test.dapuk.dicodingstory.data.response.StoryResponse

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isErr = MutableLiveData<String>()
    val isErr: LiveData<String> = _isErr

//    init {
//        getStories()
//    }
//
//    fun getStories() {
//        viewModelScope.launch {
//
//            _isLoading.value = true
//            try {
//                val response = storyRepository.getStories()
//                if (response != null) {
//                    _listStories.value = response.listStory
//                } else {
//                    _isErr.value = "Gagal Fetch Story"
//                    Log.e("Failed getStories", "respons null")
//                }
//            } catch (e: Exception) {
//                _isErr.value = "Tidak ada internet"
//                Log.e("MainViewmodel", "invaldi")
//            }
//            _isLoading.value = false
//        }

    val story: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStoriesPaging().cachedIn(viewModelScope)

}