package ru.topbun.main

import ru.topbun.domain.entity.mod.ModType
import ru.topbun.ui.R

enum class ModTypeUi(val titleRes: Int) {

    ALL(R.string.sort_type_enum_all),
    ADDON(R.string.sort_type_enum_addon),
    MAPS(R.string.sort_type_enum_maps),
    TEXTURE(R.string.sort_type_enum_texture),
    SKINS(R.string.sort_type_enum_skins);

    fun toModSortType() = when(this){
        ModTypeUi.ALL -> null
        ModTypeUi.ADDON -> ModType.ADDON
        ModTypeUi.MAPS -> ModType.WORLD
        ModTypeUi.TEXTURE -> ModType.TEXTURE_PACK
        ModTypeUi.SKINS -> ModType.SKIN_PACK
    }

}