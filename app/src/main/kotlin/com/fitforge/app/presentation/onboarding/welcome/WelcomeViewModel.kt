package com.fitforge.app.presentation.onboarding.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.domain.usecase.onboarding.GetWelcomePagesUseCase
import com.fitforge.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val getWelcomePagesUseCase: GetWelcomePagesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(pages = getWelcomePagesUseCase())
            }
        }
    }

    fun onGetStartedClick() {
        _uiState.update { current ->
            current.copy(destinationRoute = Screen.GoalSelection.route)
        }
    }
}
