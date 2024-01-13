package com.example.riffrift.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riffrift.Repository.Repository
import com.example.riffrift.Retrofit.TrackData
import kotlinx.coroutines.launch

class RetrofitViewModel(private val repository: Repository) : ViewModel() {

    private val _trackData = MutableLiveData<TrackData>()
    val trackData: LiveData<TrackData> get() = _trackData

    private val _errorMessage = MutableLiveData<String>()
    fun fetchData(query: String) {
        viewModelScope.launch {
            _trackData.value = repository.getDataFromRetrofit(query)
        }
    }

    var query by mutableStateOf("")
}
