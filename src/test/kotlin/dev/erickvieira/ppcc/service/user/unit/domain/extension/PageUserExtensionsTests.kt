package dev.erickvieira.ppcc.service.user.unit.domain.extension

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.domain.extension.fromPage
import dev.erickvieira.ppcc.service.user.extension.load
import dev.erickvieira.ppcc.service.user.unit.UserUnitTests
import dev.erickvieira.ppcc.service.user.web.api.model.PageUserDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PageUserExtensionsTests : UserUnitTests() {

    @Test
    fun `toPageUserDTO - must return a PageUserDTO instance`() {
        val users = listOf(
            User.randomize(fullName = "A"),
            User.randomize(fullName = "B"),
            User.randomize(fullName = "C")
        )
        val userPage = generateUserPage(content = users)

        load<PageUserDTO> { fromPage(page = userPage) }.let { pageUserDTO ->
            pageUserDTO.assertPagination(page = userPage)
            assert(pageUserDTO.content.filter { user ->
                users.find { it.id == user.id && it.fullName == user.fullName } != null
            }.size == users.size)
        }
    }

}