package ru.topbun.android.ads.banner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.topbun.android.ads.banner.applovin.ApplovinBannerAdView
import ru.topbun.android.ads.banner.yandex.YandexBannerAdView
import ru.topbun.android.utills.LocationAd
import ru.topbun.domain.entity.ConfigEntity

object BannerAdInitializer {

    private var initialized by mutableStateOf(false)
    private var activeNetwork by mutableStateOf<Network>(Network.None)

    private sealed interface Network {
        object None : Network
        data class Applovin(val adId: String) : Network
        data class Yandex(val adId: String) : Network
    }

    fun init(location: LocationAd, config: ConfigEntity) {
        if (initialized) return

        activeNetwork = if (location == LocationAd.OTHER) {
            config.applovinBanner?.let { Network.Applovin(it) } ?: Network.None
        } else {
            config.yandexBanner?.let { Network.Yandex(it) } ?: Network.None
        }
        initialized = true
    }

    @Composable
    fun Show(modifier: Modifier = Modifier) {
        if (!initialized) return

        when (val network = activeNetwork) {
            is Network.Applovin -> ApplovinBannerAdView(network.adId, modifier)
            is Network.Yandex -> YandexBannerAdView(network.adId, modifier)
            Network.None -> {}
        }
    }

    fun onDestroy() {
        initialized = false
        activeNetwork = Network.None
    }

}
