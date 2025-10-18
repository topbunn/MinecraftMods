package ru.topbun.data.api.dto

data class GetModsResponse(
    val count: Int,
    val mods: List<ModDto>
)
