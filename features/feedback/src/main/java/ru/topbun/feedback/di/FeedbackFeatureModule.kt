package ru.topbun.feedback.di

import org.koin.dsl.module
import ru.topbun.feedback.FeedbackViewModel

val feedbackFeatureModule = module {
    factory { FeedbackViewModel(get()) }
}