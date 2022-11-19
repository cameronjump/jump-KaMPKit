package co.touchlab.kampkit.base

/**
 * Enum class that holds a key and length of time for stale data
 */
enum class StaleDataKey(val keyName: String, val msUntilStale: Int) {
    BreedStaleData("BreedTimestampKey", ONE_MINUTE_MS * 10);
}

private const val ONE_MINUTE_MS = 60 * 1000
