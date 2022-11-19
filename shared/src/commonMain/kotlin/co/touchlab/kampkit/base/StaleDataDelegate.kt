package co.touchlab.kampkit.base

import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock

/**
 * Handles updating/checking if data is stale given a [StaleDataKey] based on system time
 * and stored [settings].
 */
class StaleDataDelegate(
    private val settings: Settings,
    private val clock: Clock
) {

    fun updateLastTime(key: StaleDataKey) {
        settings.putLong(key.keyName, clock.now().toEpochMilliseconds())
    }

    fun isDataStale(key: StaleDataKey): Boolean {
        val lastDownloadTimeMS = settings.getLong(key.keyName, 0)
        return lastDownloadTimeMS + key.msUntilStale < clock.now().toEpochMilliseconds()
    }
}
