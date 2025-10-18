package ru.topbun.detail_mod

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.android.utills.getModNameFromUrl
import ru.topbun.data.DownloadState
import ru.topbun.data.database.entity.FavoriteEntity
import ru.topbun.data.repository.ModRepository
import ru.topbun.detail_mod.DetailModState.DownloadModState
import ru.topbun.detail_mod.DetailModState.DownloadModState.Idle
import ru.topbun.detail_mod.DetailModState.LoadModState.Error
import ru.topbun.detail_mod.DetailModState.LoadModState.Loading
import ru.topbun.detail_mod.DetailModState.LoadModState.Success
import ru.topbun.ui.R
import java.io.File

class DetailModViewModel(context: Context, private val modId: Int) : ViewModel() {

    private val repository = ModRepository(context)

    private val _state = MutableStateFlow(DetailModState())
    val state get() = _state.asStateFlow()

    init {
        loadConfig()
        loadMod()
    }

    private fun loadConfig() = viewModelScope.launch {
        val config = repository.getConfig()
        _state.update { it.copy(config = config) }
    }

    fun loadMod() = viewModelScope.launch {
        _state.update { it.copy(loadModState = Loading) }
        val result = repository.getMod(modId)
        result.onSuccess { mod ->
            _state.update { it.copy(mod = mod, loadModState = Success) }
        }.onFailure {
            _state.update { it.copy(loadModState = Error("Loading error. Check internet connection")) }
        }

    }

    fun changeStageSetupMod(path: String?) = _state.update {
        it.copy(
            choiceFilePathSetup = path,
            downloadState = Idle
        )
    }

    fun switchDescriptionImageExpand() = _state.update {
        it.copy(descriptionImageExpand = !state.value.descriptionImageExpand)
    }

    fun switchDescriptionExpand() = _state.update {
        it.copy(descriptionTextExpand = !state.value.descriptionTextExpand)
    }

    fun openDontWorkDialog(value: Boolean) = _state.update {
        it.copy(dontWorkAddonDialogIsOpen = value)
    }

    fun downloadFile() = viewModelScope.launch(CoroutineExceptionHandler { _, _ -> }) {
        _state.value.mod?.let { mod ->
            _state.value.choiceFilePathSetup?.let {
                val result =
                    repository.downloadFile(it, it.getModNameFromUrl(mod.category.toExtension()))
                result.onSuccess { downloadFlow ->
                    downloadFlow.collect {
                        val downloadState = when (val state = it) {
                            is DownloadState.Downloading -> DownloadModState.Loading(state.progress)
                            is DownloadState.Failed -> DownloadModState.Error("Download error. Check Internet connection")
                            DownloadState.Finished -> DownloadModState.Success
                        }
                        _state.update { it.copy(downloadState = downloadState) }
                    }
                }.onFailure { error ->
                    _state.update { it.copy(downloadState = DownloadModState.Error("Download error. Check Internet connection")) }
                }
            }
        }
    }


    fun showInAppReview(activity: Activity) {
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }

    fun installMod(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/octet-stream")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            setPackage("com.mojang.minecraftpe")
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.minecraft_is_not_installed),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun changeFavorite() = viewModelScope.launch {
        state.value.mod?.let { mod ->
            val newFavorite = FavoriteEntity(
                modId = mod.id,
                status = !mod.isFavorite
            )
            repository.addFavorite(newFavorite)
            val newMod = mod.copy(isFavorite = newFavorite.status)
            _state.update { it.copy(mod = newMod) }
        }
    }

}