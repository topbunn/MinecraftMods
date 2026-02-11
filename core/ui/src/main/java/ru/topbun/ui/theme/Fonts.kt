package ru.topbun.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import ru.topbun.ui.R

object Fonts {

    private fun createFont(fontRes: Int) = FontFamily(Font(fontRes))

    object INTER {

        val BOLD = createFont(R.font.inter_bold)
        val SEMI_BOLD = createFont(R.font.inter_semibold)
        val REGULAR = createFont(R.font.inter_regular)
        val MEDIUM = createFont(R.font.inter_medium)

    }

}