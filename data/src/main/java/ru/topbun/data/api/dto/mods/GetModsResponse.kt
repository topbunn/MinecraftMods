package ru.topbun.data.api.dto.mods

data class GetModsResponse(
    val count: Int,
    val mods: List<ModDto>
)
