package br.com.cardapio.app

import androidx.compose.runtime.Composable

@Composable
actual fun rememberWindowSize(): WindowSize {
    return WindowSize.EXPANDED
}