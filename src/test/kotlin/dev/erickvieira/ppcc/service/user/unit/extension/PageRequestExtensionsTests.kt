package dev.erickvieira.ppcc.service.user.unit.extension

import dev.erickvieira.ppcc.service.user.extension.PageRequest
import dev.erickvieira.ppcc.service.user.web.api.model.Direction
import dev.erickvieira.ppcc.service.user.web.api.model.UserFields
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PageRequestExtensionsTests {

    @Test
    fun `PageRequest - must return a new instance based on parameters map`() {
        mapOf(
            "page" to 1,
            "size" to 20,
            "sort" to UserFields.fullName,
            "direction" to Direction.asc
        ).let { parameters ->
            PageRequest(parameters).let { pageable ->
                assertEquals(parameters["page"], pageable.pageNumber)
                assertEquals(parameters["size"], pageable.pageSize)
                assertEquals(
                    "${parameters["sort"]}: ${(parameters["direction"] as Direction).value.uppercase()}",
                    pageable.sort.toString()
                )
            }
        }
    }

    @Test
    fun `PageRequest - must return a new instance according to map properties fallbacks`() {
        mapOf<String, String>().let { parameters ->
            PageRequest(parameters).let { pageable ->
                assertEquals(0, pageable.pageNumber)
                assertEquals(20, pageable.pageSize)
                assertEquals("fullName: ASC", pageable.sort.toString())
            }
        }
    }

}