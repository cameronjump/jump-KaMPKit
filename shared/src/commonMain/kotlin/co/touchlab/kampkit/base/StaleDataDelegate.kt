package co.touchlab.kampkit.base

/**
 * Handles updating/checking if data is stale given a [StaleDataKey]
 */
interface StaleDataDelegate {

    fun updateLastTime(key: StaleDataKey)

    fun isDataStale(key: StaleDataKey): Boolean
}
