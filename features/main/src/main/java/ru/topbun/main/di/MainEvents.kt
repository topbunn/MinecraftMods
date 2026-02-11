package ru.topbun.main.di

sealed interface MainEvents {
    object ShowError: MainEvents
}