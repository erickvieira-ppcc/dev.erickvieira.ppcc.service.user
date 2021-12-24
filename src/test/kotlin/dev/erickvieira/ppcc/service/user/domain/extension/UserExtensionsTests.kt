package dev.erickvieira.ppcc.service.user.domain.extension

import dev.erickvieira.ppcc.service.user.UserUnitTests
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UserExtensionsTests : UserUnitTests() {

    @Test
    fun `toUserDTO - must return an UserDTO according to both User X and Y`() {
        val userX = generateUserX()
        val userY = generateUserY()

        userX.toUserDTO().let { userDTOX ->
            assertNotNull(userX.id)
            assertNotNull(userDTOX.id)
            assertEquals(userX.id, userDTOX.id)
            assertEquals(userX.cpf, userDTOX.cpf)
            assertEquals(userX.name, userDTOX.name)
            assertEquals(userX.birthDate, userDTOX.birthDate)
            assertEquals(userX.phone, userDTOX.phone)
            assertEquals(userX.email, userDTOX.email)
            assertEquals(userX.createdAt, userDTOX.createdAt)
            assertEquals(userX.updatedAt, userDTOX.updatedAt)
        }

        userY.toUserDTO().let { userDTOY ->
            assertNotNull(userY.id)
            assertNotNull(userDTOY.id)
            assertEquals(userY.id, userDTOY.id)
            assertEquals(userY.cpf, userDTOY.cpf)
            assertEquals(userY.name, userDTOY.name)
            assertEquals(userY.birthDate, userDTOY.birthDate)
            assertEquals(userY.phone, userDTOY.phone)
            assertEquals(userY.email, userDTOY.email)
            assertEquals(userY.createdAt, userDTOY.createdAt)
            assertEquals(userY.updatedAt, userDTOY.updatedAt)
        }
    }

    @Test
    fun `withUpdatedValues - must return an updated User according to an UserPartialUpdateDTO instance`() {
        val user = generateUserX()
        val userPartialUpdateDTO = generateUserPartialUpdateDTO(
            name = "A",
            birthDate = LocalDate.of(1999, 4, 1),
            phone = null,
            email = null
        )

        user.withUpdatedValues(userPartialUpdateDTO).let { updatedUser ->
            assert(user !== updatedUser)
            assertEquals(user.id, updatedUser.id)
            assertEquals(user.cpf, updatedUser.cpf)
            assertNotEquals(user.name, updatedUser.name)
            assertNotEquals(user.birthDate, updatedUser.birthDate)
            assertEquals(user.email, updatedUser.email)
            assertEquals(user.phone, updatedUser.phone)
            assertEquals(user.createdAt, updatedUser.createdAt)
            assertNotNull(updatedUser.updatedAt)
        }
    }

    @Test
    fun `withUpdatedValues - must return an updated User according to an UserUpdateDTO instance`() {
        val user = generateUserX()
        val userUpdateDTO = generateUserUpdateDTO(
            name = "A",
            birthDate = LocalDate.of(1999, 4, 1),
            phone = null,
            email = null
        )

        user.withUpdatedValues(userUpdateDTO).let { updatedUser ->
            assert(user !== updatedUser)
            assertEquals(user.id, updatedUser.id)
            assertEquals(user.cpf, updatedUser.cpf)
            assertNotEquals(user.name, updatedUser.name)
            assertNotEquals(user.birthDate, updatedUser.birthDate)
            assertNull(updatedUser.email)
            assertNull(updatedUser.phone)
            assertEquals(user.createdAt, updatedUser.createdAt)
            assertNotNull(updatedUser.updatedAt)
        }
    }

    @Test
    fun `asDeleted - must return an User instance with non-null deletedAt date`() {
        val user = generateUser()

        user.asDeleted().let { deletedUser ->
            assert(user !== deletedUser)
            assertNotNull(deletedUser.deletedAt)
        }
    }

    private fun generateUserX() = generateUser(
        name = "User X",
        birthDate = LocalDate.of(2001, 10, 1),
        cpf = "77766655544",
        email = "user-x@email.com",
        phone = "999998888"
    )

    private fun generateUserY() = generateUser(
        name = "User Y",
        birthDate = LocalDate.of(1970, 10, 1),
        cpf = "11122233344",
        email = "user-y@email.com",
        phone = "988889999"
    )

}