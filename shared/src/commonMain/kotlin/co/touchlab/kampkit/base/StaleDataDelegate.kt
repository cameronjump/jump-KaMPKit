package co.touchlab.kampkit.base

interface StaleDataDelegate {

    fun updateLastTime(key: StaleDataKey)

    fun isDataStale(key: StaleDataKey): Boolean
}
