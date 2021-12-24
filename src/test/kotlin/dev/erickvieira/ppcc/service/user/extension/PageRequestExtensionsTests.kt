package dev.erickvieira.ppcc.service.user.extension

import dev.erickvieira.ppcc.service.user.web.api.model.Direction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PageRequestExtensionsTests {

    @Test
    fun `PageRequest - must return a new instance based on parameters map`() {
        mapOf(
            "page" to 1,
            "size" to 20,
            "sort" to "name",
            "direction" to Direction.ASC
        ).let { parameters ->
            PageRequest(parameters).let { pageable ->
                assertEquals(parameters["page"], pageable.pageNumber)
                assertEquals(parameters["size"], pageable.pageSize)
                assertEquals("${parameters["sort"]}: ${parameters["direction"]}", pageable.sort.toString())
            }
        }
    }

    @Test
    fun `PageRequest - must return a new instance according to map properties fallbacks`() {
        mapOf<String, String>().let { parameters ->
            PageRequest(parameters).let { pageable ->
                assertEquals(0, pageable.pageNumber)
                assertEquals(20, pageable.pageSize)
                assertEquals("name: ASC", pageable.sort.toString())
            }
        }
    }

}