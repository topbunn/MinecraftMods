package ru.topbun.detail_mod.dontWorkAddon

sealed interface DontWorkAddonEvent{
    data object ShowSuccess: DontWorkAddonEvent
    data class ShowError(val msg: String): DontWorkAddonEvent
}