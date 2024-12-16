package test.dapuk.dicodingstory.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import test.dapuk.dicodingstory.data.repository.StoryRepository
import test.dapuk.dicodingstory.ui.detail.DetailViewModel
import test.dapuk.dicodingstory.ui.main.MainViewModel
import test.dapuk.dicodingstory.ui.maps.MapsViewModel

class ViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapsViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("unknown viemodel classs")
    }

}