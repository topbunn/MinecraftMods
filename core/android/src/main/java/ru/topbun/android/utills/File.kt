package ru.topbun.android.utills

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

fun getModFile(fileName: String): File? {
    val downloadsDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "mods"
    )
    val targetFile = File(downloadsDir, fileName)
    return if (targetFile.exists() && targetFile.isFile) targetFile else null
}


fun String.getModNameFromUrl(type: String = "") = substringBeforeLast("/?")
    .substringAfterLast('/')
    .substringBeforeLast('.')
    .replace("%20", " ") + type
