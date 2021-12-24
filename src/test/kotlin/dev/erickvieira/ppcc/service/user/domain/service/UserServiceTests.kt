package dev.erickvieira.ppcc.service.user.domain.service

import dev.erickvieira.ppcc.service.user.UserUnitTests
import dev.erickvieira.ppcc.service.user.domain.exception.DuplicatedCpfException
import dev.erickvieira.ppcc.service.user.domain.exception.InvalidPayloadException
import dev.erickvieira.ppcc.service.user.domain.exception.UserNotFoundException
import dev.erickvieira.ppcc.service.user.domain.extension.asDeleted
import dev.erickvieira.ppcc.service.user.domain.extension.toUser
import dev.erickvieira.ppcc.service.user.domain.extension.withUpdatedValues
import dev.erickvieira.ppcc.service.user.domain.repository.UserRepository
import dev.erickvieira.ppcc.service.user.web.api.model.Direction
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDate
import java.util.*

class UserServiceTests : UserUnitTests() {

    private val userRepositoryMock: UserRepository = mockk()
    private val userServiceMock = UserService(userRepository = userRepositoryMock)

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `searchUsers - must return a page of all users`() {
        val pagedResultMock = generateUserPage()

        every {
            userRepositoryMock.findAllByDeletedAtIsNull(pageable = any())
        } returns pagedResultMock

        userServiceMock.searchUsersWithNullFallback().let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(pagedResultMock.number, responseBody.currentPage)
                assertEquals(pagedResultMock.size, responseBody.pageSize)
                assertEquals(pagedResultMock.totalPages, responseBody.pageCount)
                assertEquals(pagedResultMock.totalElements, responseBody.total)
                assertEquals(pagedResultMock.numberOfElements, responseBody.content.size)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `searchUsers - must return a page of users according to name`() {
        val pagedResultMock = generateUserPage()

        every {
            userRepositoryMock.findAllByNameAndDeletedAtIsNull(name = any(), pageable = any())
        } returns pagedResultMock

        userServiceMock.searchUsersWithNullFallback(name = "Erick").let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(pagedResultMock.number, responseBody.currentPage)
                assertEquals(pagedResultMock.size, responseBody.pageSize)
                assertEquals(pagedResultMock.totalPages, responseBody.pageCount)
                assertEquals(pagedResultMock.totalElements, responseBody.total)
                assertEquals(pagedResultMock.numberOfElements, responseBody.content.size)
            }
        }

        verify(exactly = 0) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.findAllByNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `searchUsers - must return a page of users according to CPF`() {
        val pagedResultMock = generateUserPage()

        every {
            userRepositoryMock.findAllByCpfAndDeletedAtIsNull(cpf = any(), pageable = any())
        } returns pagedResultMock

        userServiceMock.searchUsersWithNullFallback(cpf = "77766655544").let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(pagedResultMock.number, responseBody.currentPage)
                assertEquals(pagedResultMock.size, responseBody.pageSize)
                assertEquals(pagedResultMock.totalPages, responseBody.pageCount)
                assertEquals(pagedResultMock.totalElements, responseBody.total)
                assertEquals(pagedResultMock.numberOfElements, responseBody.content.size)
            }
        }

        verify(exactly = 0) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 1) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `searchUsers - must return a page of users according to both name and CPF`() {
        val pagedResultMock = generateUserPage()

        every {
            userRepositoryMock.findAllByCpfAndNameAndDeletedAtIsNull(name = any(), cpf = any(), pageable = any())
        } returns pagedResultMock

        userServiceMock.searchUsersWithNullFallback(name = "Robert", cpf = "77766655544").let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(pagedResultMock.number, responseBody.currentPage)
                assertEquals(pagedResultMock.size, responseBody.pageSize)
                assertEquals(pagedResultMock.totalPages, responseBody.pageCount)
                assertEquals(pagedResultMock.totalElements, responseBody.total)
                assertEquals(pagedResultMock.numberOfElements, responseBody.content.size)
            }
        }

        verify(exactly = 0) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 1) { userRepositoryMock.findAllByCpfAndNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `searchUsers - must throw UserNotFoundException`() {
        val pagedResultMock = generateEmptyUserPage()

        every {
            userRepositoryMock.findAllByNameAndDeletedAtIsNull(name = any(), pageable = any())
        } returns pagedResultMock

        assertThrows<UserNotFoundException> { userServiceMock.searchUsersWithNullFallback(name = "Bia") }

        verify(exactly = 0) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.findAllByNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `createUser - must return the newly created user`() {
        val userCreationDTOMock = generateUserCreationDTO()
        val userMock = userCreationDTOMock.toUser()

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByCpf(cpf = any()) } returns null
        every { userRepositoryMock.save(any()) } returns userCreationDTOMock.toUser()

        userServiceMock.createUser(userCreationDTOMock).let { responseEntity ->
            assertEquals(201, responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(userMock.name, responseBody.name)
                assertEquals(userMock.birthDate, responseBody.birthDate)
                assertEquals(userMock.email, responseBody.email)
                assertEquals(userMock.phone, responseBody.phone)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByCpf(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `createUser - must throw DuplicatedCpfException`() {
        val userCreationDTOMock = generateUserCreationDTO()
        val userMock = userCreationDTOMock.toUser()

        every { userRepositoryMock.findFirstByCpf(cpf = any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns mockk()

        assertThrows<DuplicatedCpfException> { userServiceMock.createUser(userCreationDTOMock) }

        verify(exactly = 1) { userRepositoryMock.findFirstByCpf(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `retrieveUser - must return the user according to its UUID`() {
        val userMock = generateUser()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) } returns userMock

        userServiceMock.retrieveUser(userMock.id!!).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(userMock.id, responseBody.id)
                assertEquals(userMock.name, responseBody.name)
                assertEquals(userMock.birthDate, responseBody.birthDate)
                assertEquals(userMock.email, responseBody.email)
                assertEquals(userMock.phone, responseBody.phone)
                assertEquals(userMock.createdAt, responseBody.createdAt)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
    }

    @Test
    fun `retrieveUser - must throw UserNotFoundException`() {
        val userMock = generateUser()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) } returns null

        assertThrows<UserNotFoundException> { userServiceMock.retrieveUser(userMock.id!!) }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
    }

    @Test
    fun `retrieveUserByCpf - must return the user according to its CPF`() {
        val userMock = generateUser()

        every { userRepositoryMock.findFirstByCpfAndDeletedAtIsNull(any()) } returns userMock

        userServiceMock.retrieveUserByCpf(userMock.cpf).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(userMock.id, responseBody.id)
                assertEquals(userMock.name, responseBody.name)
                assertEquals(userMock.birthDate, responseBody.birthDate)
                assertEquals(userMock.email, responseBody.email)
                assertEquals(userMock.phone, responseBody.phone)
                assertEquals(userMock.createdAt, responseBody.createdAt)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByCpfAndDeletedAtIsNull(any()) }
    }

    @Test
    fun `retrieveUserByCpf - must throw UserNotFoundException`() {
        val userMock = generateUser()

        every { userRepositoryMock.findFirstByCpfAndDeletedAtIsNull(any()) } returns null

        assertThrows<UserNotFoundException> { userServiceMock.retrieveUserByCpf(userMock.cpf) }

        verify(exactly = 1) { userRepositoryMock.findFirstByCpfAndDeletedAtIsNull(any()) }
    }

    @Test
    fun `updateUser - must return the updated user`() {
        val userUpdateDTOMock = generateUserUpdateDTO(
            name = "Roberto",
            birthDate = LocalDate.of(1998, 10, 2),
            email = "another@email.com",
            phone = "987789191"
        )
        val userMock = generateUser()
        val updatedUserMock = userMock.withUpdatedValues(userUpdateDTOMock)

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns updatedUserMock

        userServiceMock.updateUser(userMock.id!!, userUpdateDTOMock).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(userMock.cpf, updatedUserMock.cpf)
                assertEquals(updatedUserMock.cpf, responseBody.cpf)
                assertEquals(userMock.cpf, updatedUserMock.cpf)
                assertEquals(updatedUserMock.cpf, responseBody.cpf)
                assertNotEquals(userMock.name, responseBody.name)
                assertEquals(updatedUserMock.name, responseBody.name)
                assertNotEquals(userMock.birthDate, responseBody.birthDate)
                assertEquals(updatedUserMock.birthDate, responseBody.birthDate)
                assertNotEquals(userMock.email, responseBody.email)
                assertEquals(updatedUserMock.email, responseBody.email)
                assertNotEquals(userMock.phone, responseBody.phone)
                assertEquals(updatedUserMock.phone, responseBody.phone)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `updateUser - must return the updated user with a null e-mail`() {
        val userUpdateDTOMock = generateUserUpdateDTO(
            name = "Roberto",
            birthDate = LocalDate.of(1998, 10, 2),
            phone = "987789191"
        )
        val userMock = generateUser()
        val updatedUserMock = userMock.withUpdatedValues(userUpdateDTOMock)

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns updatedUserMock

        userServiceMock.updateUser(userMock.id!!, userUpdateDTOMock).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(userMock.cpf, updatedUserMock.cpf)
                assertEquals(updatedUserMock.cpf, responseBody.cpf)
                assertEquals(userMock.cpf, updatedUserMock.cpf)
                assertEquals(updatedUserMock.cpf, responseBody.cpf)
                assertNotEquals(userMock.name, responseBody.name)
                assertEquals(updatedUserMock.name, responseBody.name)
                assertNotEquals(userMock.birthDate, responseBody.birthDate)
                assertEquals(updatedUserMock.birthDate, responseBody.birthDate)
                assertNull(responseBody.email)
                assertNotEquals(userMock.phone, responseBody.phone)
                assertEquals(updatedUserMock.phone, responseBody.phone)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `updateUser - must throw UserNotFoundException`() {
        val userUpdateDTOMock = generateUserUpdateDTO()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns null
        every { userRepositoryMock.save(any()) } returns mockk()

        assertThrows<UserNotFoundException> { userServiceMock.updateUser(UUID.randomUUID(), userUpdateDTOMock) }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `updateUser - must throw InvalidPayloadException`() {
        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns mockk()
        every { userRepositoryMock.save(any()) } returns mockk()

        assertThrows<InvalidPayloadException> { userServiceMock.updateUser(UUID.randomUUID(), null) }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `partiallyUpdateUser - must return the updated user`() {
        val userPartialUpdateDTOMock = generateUserPartialUpdateDTO(
            name = "Roberto",
            birthDate = LocalDate.of(1998, 10, 2),
            email = "another@email.com",
            phone = "987789191"
        )
        val userMock = generateUser()
        val updatedUserMock = userMock.withUpdatedValues(userPartialUpdateDTOMock)

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns updatedUserMock

        userServiceMock.partiallyUpdateUser(userMock.id!!, userPartialUpdateDTOMock).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(userMock.id, updatedUserMock.id)
                assertEquals(updatedUserMock.id, responseBody.id)
                assertEquals(userMock.cpf, updatedUserMock.cpf)
                assertEquals(updatedUserMock.cpf, responseBody.cpf)
                assertNotEquals(userMock.name, responseBody.name)
                assertEquals(updatedUserMock.name, responseBody.name)
                assertNotEquals(userMock.birthDate, responseBody.birthDate)
                assertEquals(updatedUserMock.birthDate, responseBody.birthDate)
                assertNotEquals(userMock.email, responseBody.email)
                assertEquals(updatedUserMock.email, responseBody.email)
                assertNotEquals(userMock.phone, responseBody.phone)
                assertEquals(updatedUserMock.phone, responseBody.phone)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `partiallyUpdateUser - must return the updated user with the same previous e-mail`() {
        val userPartialUpdateDTOMock = generateUserPartialUpdateDTO(
            name = "Roberto",
            birthDate = LocalDate.of(1998, 10, 2),
            phone = "987789191"
        )
        val userMock = generateUser()
        val updatedUserMock = userMock.withUpdatedValues(userPartialUpdateDTOMock)

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns updatedUserMock

        userServiceMock.partiallyUpdateUser(userMock.id!!, userPartialUpdateDTOMock).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(userMock.cpf, updatedUserMock.cpf)
                assertEquals(updatedUserMock.cpf, responseBody.cpf)
                assertEquals(userMock.cpf, updatedUserMock.cpf)
                assertEquals(updatedUserMock.cpf, responseBody.cpf)
                assertNotEquals(userMock.name, responseBody.name)
                assertEquals(updatedUserMock.name, responseBody.name)
                assertNotEquals(userMock.birthDate, responseBody.birthDate)
                assertEquals(updatedUserMock.birthDate, responseBody.birthDate)
                assertEquals(userMock.email, updatedUserMock.email)
                assertEquals(updatedUserMock.email, responseBody.email)
                assertNotEquals(userMock.phone, responseBody.phone)
                assertEquals(updatedUserMock.phone, responseBody.phone)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `partiallyUpdateUser - must throw UserNotFoundException`() {
        val userPartialUpdateDTOMock = generateUserPartialUpdateDTO()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns null
        every { userRepositoryMock.save(any()) } returns mockk()

        assertThrows<UserNotFoundException> {
            userServiceMock.partiallyUpdateUser(UUID.randomUUID(), userPartialUpdateDTOMock)
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `partiallyUpdateUser - must throw InvalidPayloadException`() {
        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns mockk()
        every { userRepositoryMock.save(any()) } returns mockk()

        assertThrows<InvalidPayloadException> { userServiceMock.updateUser(UUID.randomUUID(), null) }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `deleteUser - must return the deleted user`() {
        val userMock = generateUser()
        val deletedUserMock = userMock.asDeleted()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns deletedUserMock

        userServiceMock.deleteUser(userMock.id!!).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                assertEquals(deletedUserMock.id, responseBody.id)
                assertEquals(deletedUserMock.name, responseBody.name)
                assertEquals(deletedUserMock.birthDate, responseBody.birthDate)
                assertEquals(deletedUserMock.email, responseBody.email)
                assertEquals(deletedUserMock.phone, responseBody.phone)
                assertEquals(deletedUserMock.createdAt, responseBody.createdAt)
                assertNotNull(deletedUserMock.deletedAt)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `deleteUser - must throw UserNotFoundException`() {
        val userMock = generateUser()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) } returns null

        assertThrows<UserNotFoundException> { userServiceMock.deleteUser(userMock.id!!) }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    private fun UserService.searchUsersWithNullFallback(
        name: String? = null,
        cpf: String? = null,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null,
        direction: Direction? = null
    ) = this.searchUsers(name = name, cpf = cpf, page = page, size = size, sort = sort, direction = direction)

}