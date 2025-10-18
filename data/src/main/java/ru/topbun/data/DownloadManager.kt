package ru.topbun.data

import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.File


sealed class DownloadState {
    data class Downloading(val progress: Int) : DownloadState()
    object Finished : DownloadState()
    data class Failed(val error: Throwable? = null) : DownloadState()
}


fun ResponseBody.saveFile(fileName: String): Flow<DownloadState> {
    return flow{
        emit(DownloadState.Downloading(0))

        val downloadsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "mods")
        if (!downloadsDir.exists()) downloadsDir.mkdirs()

        val destinationFile = File(downloadsDir, fileName)

        try {
            byteStream().use { inputStream->
                destinationFile.outputStream().use { outputStream->
                    val totalBytes = contentLength()
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var progressBytes = 0L
                    var bytes = inputStream.read(buffer)
                    while (bytes >= 0) {
                        outputStream.write(buffer, 0, bytes)
                        progressBytes += bytes
                        bytes = inputStream.read(buffer)
                        emit(DownloadState.Downloading(((progressBytes * 100) / totalBytes).toInt()))
                    }
                }
            }
            emit(DownloadState.Finished)
        } catch (e: Exception) {
            emit(DownloadState.Failed(e))
        }
    }
        .flowOn(Dispatchers.IO).distinctUntilChanged()
}