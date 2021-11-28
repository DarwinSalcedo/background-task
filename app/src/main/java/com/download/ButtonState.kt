package com.download


sealed class ButtonState {
    object Init : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}