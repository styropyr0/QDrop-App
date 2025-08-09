package com.matrix.qdrop.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.matrix.qdrop.Repository
import kotlinx.coroutines.launch
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.matrix.qdrop.core.AppStates
import com.matrix.qdrop.models.OrgInfo

class AuthViewModel(private var repository: Repository) : ViewModel() {
    var orgInfo: MutableState<OrgInfo?> = mutableStateOf(null)
    var state: MutableState<AppStates> = mutableStateOf(AppStates.INIT)

    fun checkOrgStatus(orgId: String) {
        viewModelScope.launch {
            state.value = AppStates.LOADING
            orgInfo.value = null
            val resp = repository.validateOrganization(orgId)
            state.value = resp.state
            orgInfo.value = resp.data as? OrgInfo
        }
    }
}