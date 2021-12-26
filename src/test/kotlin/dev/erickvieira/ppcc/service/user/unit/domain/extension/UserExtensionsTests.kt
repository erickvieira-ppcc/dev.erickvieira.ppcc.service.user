package dev.erickvieira.ppcc.service.user.unit.domain.extension

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.unit.UserUnitTests
import dev.erickvieira.ppcc.service.user.domain.extension.asDeleted
import dev.erickvieira.ppcc.service.user.domain.extension.fromUserCreationDTO
import dev.erickvieira.ppcc.service.user.domain.extension.toUserDTO
import dev.erickvieira.ppcc.service.user.domain.extension.withUpdatedValues
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UserExtensionsTests : UserUnitTests() {

    @Test
    fun `toUser - must return an User instance from a given UserCreationDTO`() {
        val userCreationDTO = generateUserCreationDTO()

        User.fromUserCreationDTO(input = userCreationDTO).let { user ->
            assertNull(user.id)
            assertEquals(userCreationDTO.cpf, user.cpf)
            assertEquals(userCreationDTO.fullName, user.fullName)
            assertEquals(userCreationDTO.birthDate, user.birthDate)
            assertEquals(userCreationDTO.phone, user.phone)
            assertEquals(userCreationDTO.email, user.email)
            assertNotNull(user.createdAt)
            assertNull(user.updatedAt)
            assertNull(user.deletedAt)
        }
    }

    @Test
    fun `toUserDTO - must return an UserDTO according to both User X and Y`() {
        val userX = generateUserX()
        val userY = generateUserY()

        userX.toUserDTO().let { userDTOX ->
            assertNotNull(userX.id)
            assertNotNull(userDTOX.id)
            assertEquals(userX.id, userDTOX.id)
            assertEquals(userX.cpf, userDTOX.cpf)
            assertEquals(userX.fullName, userDTOX.fullName)
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
            assertEquals(userY.fullName, userDTOY.fullName)
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
            fullName = "A",
            birthDate = LocalDate.of(1999, 4, 1),
            phone = null,
            email = null
        )

        user.withUpdatedValues(userPartialUpdateDTO).let { updatedUser ->
            assert(user !== updatedUser)
            assertEquals(user.id, updatedUser.id)
            assertEquals(user.cpf, updatedUser.cpf)
            assertNotEquals(user.fullName, updatedUser.fullName)
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
            fullName = "A",
            birthDate = LocalDate.of(1999, 4, 1),
            phone = null,
            email = null
        )

        user.withUpdatedValues(userUpdateDTO).let { updatedUser ->
            assert(user !== updatedUser)
            assertEquals(user.id, updatedUser.id)
            assertEquals(user.cpf, updatedUser.cpf)
            assertNotEquals(user.fullName, updatedUser.fullName)
            assertNotEquals(user.birthDate, updatedUser.birthDate)
            assertNull(updatedUser.email)
            assertNull(updatedUser.phone)
            assertEquals(user.createdAt, updatedUser.createdAt)
            assertNotNull(updatedUser.updatedAt)
        }
    }

    @Test
    fun `asDeleted - must return an User instance with non-null deletedAt date`() {
        val user = User.randomize()

        user.asDeleted().let { deletedUser ->
            assert(user !== deletedUser)
            assertNotNull(deletedUser.deletedAt)
        }
    }

    private fun generateUserX() = User.randomize(
        fullName = "User X",
        birthDate = LocalDate.of(2001, 10, 1),
        cpf = "77766655544",
        email = "user-x@email.com",
        phone = "999998888"
    )

    private fun generateUserY() = User.randomize(
        fullName = "User Y",
        birthDate = LocalDate.of(1970, 10, 1),
        cpf = "11122233344",
        email = "user-y@email.com",
        phone = "988889999"
    )

}