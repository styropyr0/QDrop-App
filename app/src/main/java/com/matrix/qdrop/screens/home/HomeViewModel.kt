package com.matrix.qdrop.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matrix.qdrop.models.BuildMeta
import com.matrix.qdrop.Repository
import com.matrix.qdrop.core.UpdateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _builds = MutableStateFlow<List<BuildMeta>>(emptyList())
    val builds: StateFlow<List<BuildMeta>> = _builds

    private val _updateData = MutableStateFlow<UpdateData?>(null)
    val updateData: StateFlow<UpdateData?> = _updateData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchBuilds(orgId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.fetchBuilds(orgId)
            _builds.value = result
            _isLoading.value = false
        }
    }

    fun fetchAppUpdateData(silent: Boolean = true) {
        viewModelScope.launch {
            if (!silent)
                _isLoading.value = true
            _updateData.value = repository.getAppUpdateData()
            if (!silent)
                _isLoading.value = false
        }
    }
}