package ru.topbun.domain.entity.mod

enum class ModSortType {

    RELEVANCE,
    NAME,
    USED_COUNT,
    COMMENT_COUNTS,
    RATING;

    override fun toString() = when(this){
        RELEVANCE -> "order"
        NAME -> "name"
        USED_COUNT -> "usedCount"
        COMMENT_COUNTS -> "commentCounts"
        RATING -> "rating"
    }
}