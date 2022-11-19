package co.touchlab.kampkit.base

import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock

/**
 * Handles updating/checking if data is stale given a [StaleDataKey] based on system time
 * and stored [settings].
 */
class StaleDataDelegateImpl(
    private val settings: Settings,
    private val clock: Clock
) : StaleDataDelegate {

    override fun updateLastTime(key: StaleDataKey) {
        settings.putLong(key.keyName, clock.now().toEpochMilliseconds())
    }

    override fun isDataStale(key: StaleDataKey): Boolean {
        val lastDownloadTimeMS = settings.getLong(key.keyName, 0)
        return lastDownloadTimeMS + key.msUntilStale < clock.now().toEpochMilliseconds()
    }
}
