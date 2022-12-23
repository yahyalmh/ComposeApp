package com.example.setting

import androidx.lifecycle.viewModelScope
import com.example.common.ThemeType
import com.example.common.toThemeType
import com.example.datastore.DatastoreInteractor
import com.example.common.BaseViewModel
import com.example.common.UIEvent
import com.example.common.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val datastoreInteractor: DatastoreInteractor
) : BaseViewModel<SettingUiState, SettingUiEvent>(SettingUiState.Loading) {

    init {
        datastoreInteractor
            .getThemeType()
            .onEach {
                val themeType = it?.toThemeType() ?: ThemeType.SYSTEM
                setState(SettingUiState.SetSetting(themeType))
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: SettingUiEvent) {
        when (event) {
            is SettingUiEvent.ChangeTheme -> {
                viewModelScope.launch { datastoreInteractor.setThemeType(event.type.name) }
            }
        }
    }
}


sealed interface SettingUiEvent : UIEvent {
    class ChangeTheme(val type: ThemeType) : SettingUiEvent
}

sealed class SettingUiState(
    val isLoading: Boolean = false,
    val currentThemeType: ThemeType? = null
) : UIState {
    object Loading : SettingUiState(isLoading = true)
    class SetSetting(currentThemeType: ThemeType?) :
        SettingUiState(currentThemeType = currentThemeType)
}