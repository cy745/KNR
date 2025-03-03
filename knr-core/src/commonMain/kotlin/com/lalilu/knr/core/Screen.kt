package com.lalilu.knr.core

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle

interface Screen {

    @Composable
    fun Content(savedStateHandle: SavedStateHandle)
}