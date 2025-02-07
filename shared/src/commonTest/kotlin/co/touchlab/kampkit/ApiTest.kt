package co.touchlab.kampkit

import co.touchlab.kampkit.base.ApiStatus
import co.touchlab.kampkit.ktor.Api
import co.touchlab.kampkit.ktor.HttpClientProvider
import co.touchlab.kampkit.networkmodels.BreedDto
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import co.touchlab.kermit.Severity
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ApiTest {
    private val emptyLogger = Logger(
        config = object : LoggerConfig {
            override val logWriterList: List<LogWriter> = emptyList()
            override val minSeverity: Severity = Severity.Assert
        },
        tag = ""
    )

    @Test
    fun success() = runTest {
        val engine = MockEngine {
            assertEquals("https://dog.ceo/api/breeds/list/all", it.url.toString())
            respond(
                content = """{"message":{"affenpinscher":[],"african":["shepherd"]},"status":"success"}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClientProvider(
            emptyLogger,
            engine
        ).client
        val api = Api(emptyLogger, client)

        val result = api.getBreeds()
        assertEquals(
            ApiStatus.Success(
                BreedDto(
                    mapOf(
                        "affenpinscher" to emptyList(),
                        "african" to listOf("shepherd")
                    ),
                    "success"
                )
            ),
            result
        )
    }

    @Test
    fun failure() = runTest {
        val engine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.NotFound
            )
        }
        val client = HttpClientProvider(
            emptyLogger,
            engine
        ).client
        val api = Api(emptyLogger, client)

        assertFailsWith<ClientRequestException> {
            api.getBreeds()
        }
    }
}
