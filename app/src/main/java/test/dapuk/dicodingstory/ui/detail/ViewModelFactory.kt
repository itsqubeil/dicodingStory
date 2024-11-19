package test.dapuk.dicodingstory.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import test.dapuk.dicodingstory.data.repository.StoryRepository

class ViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("unknown viemodel classs")
    }

}