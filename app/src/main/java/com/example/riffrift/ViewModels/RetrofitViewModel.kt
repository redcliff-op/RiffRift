package com.example.riffrift.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riffrift.Repository.Repository
import com.example.riffrift.Retrofit.Data
import com.example.riffrift.Retrofit.TrackData
import kotlinx.coroutines.launch

class RetrofitViewModel(private val repository: Repository) : ViewModel() {

    var _trackData = MutableLiveData<List<Data>>()
    val trackData: LiveData<List<Data>> get() = _trackData

    private val _errorMessage = MutableLiveData<String>()
    fun fetchData(query: String) {
        viewModelScope.launch {
            _trackData.value = repository.getDataFromRetrofit(query).data!!
        }
    }

    var query by mutableStateOf("")
}
