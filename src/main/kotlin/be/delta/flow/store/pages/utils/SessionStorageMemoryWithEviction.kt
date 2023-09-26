package be.delta.flow.store.pages.utils

import io.ktor.server.sessions.*
import io.ktor.util.collections.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class SessionStorageMemoryWithEviction(
    private val timeout: Duration
) : SessionStorage {
    private val sessions = ConcurrentMap<String, TimeAndString>()

    data class TimeAndString(val timestamp: Instant, val value: String)

    init {
        CoroutineScope(EmptyCoroutineContext).launch {
            while (true) {
                delay(1.minutes.inWholeMilliseconds)
                val now = Clock.System.now()
                sessions.entries.removeIf {
                    it.value.timestamp + timeout < now
                }
            }
        }
    }

    override suspend fun invalidate(id: String) {
        sessions.remove(id)
    }

    override suspend fun read(id: String): String {
        return sessions
            .computeIfPresent(id) { _, (_, value) -> TimeAndString(Clock.System.now(), value) }
            ?.value
            ?: throw NoSuchElementException("Session $id not found")
    }

    override suspend fun write(id: String, value: String) {
        sessions.compute(id) { _, _ ->
            TimeAndString(Clock.System.now(), value)
        }
    }

}