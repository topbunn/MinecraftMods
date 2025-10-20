package ru.topbun.main

import ru.topbun.domain.entity.mod.ModSortType
import ru.topbun.ui.R

enum class ModSortTypeUi(
    val stringRes: Int
) {

    RELEVANCE(R.string.sort_enum_relevance),
    USED_COUNT(R.string.sort_enum_best),
    RATING(R.string.sort_enum_rating);

    fun toModSortType() = when(this){
        ModSortTypeUi.RELEVANCE -> ModSortType.RELEVANCE
        ModSortTypeUi.USED_COUNT -> ModSortType.COMMENT_COUNTS
        ModSortTypeUi.RATING -> ModSortType.RATING
    }
}