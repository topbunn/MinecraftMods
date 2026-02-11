package ru.topbun.feedback

sealed interface FeedbackEvent{
    data object ShowSuccess: FeedbackEvent
    data object ShowError: FeedbackEvent
}