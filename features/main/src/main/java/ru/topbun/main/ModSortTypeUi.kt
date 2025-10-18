package ru.topbun.main

import ru.topbun.domain.entity.mod.ModSortType
import ru.topbun.ui.R

enum class ModSortTypeUi(
    val stringRes: Int
) {

    NAME(R.string.sort_enum_name),
    USED_COUNT(R.string.sort_enum_best),
    COMMENT_COUNTS(R.string.sort_enum_comment),
    RATING(R.string.sort_enum_rating);

    fun toModSortType() = when(this){
        ModSortTypeUi.NAME -> ModSortType.NAME
        ModSortTypeUi.USED_COUNT -> ModSortType.USED_COUNT
        ModSortTypeUi.COMMENT_COUNTS -> ModSortType.COMMENT_COUNTS
        ModSortTypeUi.RATING -> ModSortType.RATING
    }
}