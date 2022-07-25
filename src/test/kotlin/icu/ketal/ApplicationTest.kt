package icu.ketal

import icu.ketal.data.Version
import icu.ketal.plugins.configureRouting
import icu.ketal.utils.encodeToJson
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(Version().encodeToJson(), bodyAsText())
        }
    }
}
