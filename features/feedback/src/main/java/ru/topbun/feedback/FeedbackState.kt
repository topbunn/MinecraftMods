package ru.topbun.feedback

data class FeedbackState(
    val email: String = "",
    val message: String = "",
    val sendIsValid: Boolean = false,
    val isLoading: Boolean = false
)