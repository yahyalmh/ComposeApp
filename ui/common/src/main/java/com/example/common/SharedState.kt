package com.example.common

import kotlinx.coroutines.flow.MutableSharedFlow

object SharedState {
    val bottomBarVisible = MutableSharedFlow<Boolean>()

}