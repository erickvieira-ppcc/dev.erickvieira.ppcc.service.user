package dev.erickvieira.ppcc.service.user.domain.extension

import dev.erickvieira.ppcc.service.user.UserUnitTests
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PageExtensionsTests : UserUnitTests() {

    @Test
    fun `toPageUserDTO - must return a PageUserDTO instance`() {
        val users = listOf(generateUser(name = "A"), generateUser(name = "B"), generateUser(name = "C"))
        val userPage = generateUserPage(content = users)

        userPage.toPageUserDTO().let { pageUserDTO ->
            assertEquals(userPage.number, pageUserDTO.currentPage)
            assertEquals(userPage.size, pageUserDTO.pageSize)
            assertEquals(userPage.totalPages, pageUserDTO.pageCount)
            assertEquals(userPage.totalElements, pageUserDTO.total)
            assertEquals(userPage.numberOfElements, pageUserDTO.content.size)
            assert(pageUserDTO.content.filter { user ->
                users.find { it.id == user.id && it.name == user.name } != null
            }.size == users.size)
        }
    }

}