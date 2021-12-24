package dev.erickvieira.ppcc.service.user.domain.extension

import dev.erickvieira.ppcc.service.user.UserUnitTests
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UserCreationExtensionsTests : UserUnitTests() {

    @Test
    fun `toUser - must return an User instance`() {
        val userCreationDTO = generateUserCreationDTO()

        userCreationDTO.toUser().let { user ->
            assertNull(user.id)
            assertEquals(userCreationDTO.cpf, user.cpf)
            assertEquals(userCreationDTO.name, user.name)
            assertEquals(userCreationDTO.birthDate, user.birthDate)
            assertEquals(userCreationDTO.phone, user.phone)
            assertEquals(userCreationDTO.email, user.email)
            assertNotNull(user.createdAt)
            assertNull(user.updatedAt)
            assertNull(user.deletedAt)
        }
    }

}