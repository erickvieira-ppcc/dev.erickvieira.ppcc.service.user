package dev.erickvieira.ppcc.service.user.unit.domain.service

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.unit.UserUnitTests
import dev.erickvieira.ppcc.service.user.domain.exception.DuplicatedCpfException
import dev.erickvieira.ppcc.service.user.domain.exception.NullPayloadException
import dev.erickvieira.ppcc.service.user.domain.exception.UserNotFoundException
import dev.erickvieira.ppcc.service.user.domain.extension.asDeleted
import dev.erickvieira.ppcc.service.user.domain.extension.withUpdatedValues
import dev.erickvieira.ppcc.service.user.domain.port.rabbitmq.UserRabbitDispatcherPort
import dev.erickvieira.ppcc.service.user.domain.repository.UserRepository
import dev.erickvieira.ppcc.service.user.domain.service.UserService
import dev.erickvieira.ppcc.service.user.web.api.model.Direction
import dev.erickvieira.ppcc.service.user.web.api.model.UserFields
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
    private val userDispatcherMock: UserRabbitDispatcherPort = mockk()
    private val userServiceMock = UserService(
        userRepository = userRepositoryMock,
        userDispatcher = userDispatcherMock
    )

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `searchUsers - must return a page of all users`() {
        val pagedResultMock = generateUserPage()

        every {
            userRepositoryMock.findAllByDeletedAtIsNull(pageable = any())
        } returns pagedResultMock

        userServiceMock.searchUsersWithDefaults().let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertPagination(page = pagedResultMock)
        }

        verify(exactly = 1) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByFullNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndFullNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `searchUsers - must return a page of users according to full name`() {
        val pagedResultMock = generateUserPage()

        every {
            userRepositoryMock.findAllByFullNameAndDeletedAtIsNull(fullName = any(), pageable = any())
        } returns pagedResultMock

        userServiceMock.searchUsersWithDefaults(fullName = "Erick").let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertPagination(page = pagedResultMock)
        }

        verify(exactly = 0) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.findAllByFullNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndFullNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `searchUsers - must return a page of users according to CPF`() {
        val pagedResultMock = generateUserPage()

        every {
            userRepositoryMock.findAllByCpfAndDeletedAtIsNull(cpf = any(), pageable = any())
        } returns pagedResultMock

        userServiceMock.searchUsersWithDefaults(cpf = "77766655544").let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertPagination(page = pagedResultMock)
        }

        verify(exactly = 0) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByFullNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 1) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndFullNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `searchUsers - must return a page of users according to both full name and CPF`() {
        val pagedResultMock = generateUserPage()

        every {
            userRepositoryMock.findAllByCpfAndFullNameAndDeletedAtIsNull(
                fullName = any(),
                cpf = any(),
                pageable = any()
            )
        } returns pagedResultMock

        userServiceMock.searchUsersWithDefaults(fullName = "Robert", cpf = "77766655544").let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertPagination(page = pagedResultMock)
        }

        verify(exactly = 0) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByFullNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 1) { userRepositoryMock.findAllByCpfAndFullNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `searchUsers - must throw UserNotFoundException`() {
        val pagedResultMock = generateEmptyUserPage()

        every {
            userRepositoryMock.findAllByFullNameAndDeletedAtIsNull(fullName = any(), pageable = any())
        } returns pagedResultMock

        assertThrows<UserNotFoundException> { userServiceMock.searchUsersWithDefaults(fullName = "Bia") }

        verify(exactly = 0) { userRepositoryMock.findAllByDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.findAllByFullNameAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndDeletedAtIsNull(any(), any()) }
        verify(exactly = 0) { userRepositoryMock.findAllByCpfAndFullNameAndDeletedAtIsNull(any(), any(), any()) }
    }

    @Test
    fun `createUser - must return the newly created user`() {
        val userCreationDTOMock = generateUserCreationDTO()
        val userMock = userCreationDTOMock.asSavedUser()

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByCpf(cpf = any()) } returns null
        every { userDispatcherMock.dispatch(any()) } returns Unit
        every { userRepositoryMock.save(any()) } returns userMock

        userServiceMock.createUser(userCreationDTOMock).let { responseEntity ->
            assertEquals(HttpStatus.CREATED.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertUserCreation(input = userCreationDTOMock)
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByCpf(any()) }
        verify(exactly = 1) { userDispatcherMock.dispatch(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `createUser - must throw DuplicatedCpfException`() {
        val userCreationDTOMock = generateUserCreationDTO()
        val userMock = userCreationDTOMock.asSavedUser()

        every { userRepositoryMock.findFirstByCpf(cpf = any()) } returns userMock
        every { userDispatcherMock.dispatch(any()) } returns Unit
        every { userRepositoryMock.save(any()) } returns mockk()

        assertThrows<DuplicatedCpfException> { userServiceMock.createUser(userCreationDTOMock) }

        verify(exactly = 1) { userRepositoryMock.findFirstByCpf(any()) }
        verify(exactly = 0) { userDispatcherMock.dispatch(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `retrieveUser - must return the user according to its UUID`() {
        val userMock = User.randomize()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) } returns userMock

        userServiceMock.retrieveUser(userMock.id!!).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertReturnedUser(user = userMock)
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
    }

    @Test
    fun `retrieveUser - must throw UserNotFoundException`() {
        val userMock = User.randomize()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) } returns null

        assertThrows<UserNotFoundException> { userServiceMock.retrieveUser(userMock.id!!) }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
    }

    @Test
    fun `retrieveUserByCpf - must return the user according to its CPF`() {
        val userMock = User.randomize()

        every { userRepositoryMock.findFirstByCpfAndDeletedAtIsNull(any()) } returns userMock

        userServiceMock.retrieveUserByCpf(userMock.cpf).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertReturnedUser(user = userMock)
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByCpfAndDeletedAtIsNull(any()) }
    }

    @Test
    fun `retrieveUserByCpf - must throw UserNotFoundException`() {
        val userMock = User.randomize()

        every { userRepositoryMock.findFirstByCpfAndDeletedAtIsNull(any()) } returns null

        assertThrows<UserNotFoundException> { userServiceMock.retrieveUserByCpf(userMock.cpf) }

        verify(exactly = 1) { userRepositoryMock.findFirstByCpfAndDeletedAtIsNull(any()) }
    }

    @Test
    fun `updateUser - must return the updated user`() {
        val userUpdateDTOMock = generateUserUpdateDTO(
            fullName = "Roberto",
            birthDate = LocalDate.of(1998, 10, 2),
            email = "another@email.com",
            phone = "987789191"
        )
        val userMock = User.randomize()
        val updatedUserMock = userMock.withUpdatedValues(userUpdateDTOMock)

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns updatedUserMock

        userServiceMock.updateUser(userMock.id!!, userUpdateDTOMock).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertUserUpdate(
                original = userMock,
                updated = updatedUserMock,
                UserFields.fullName,
                UserFields.birthDate,
                UserFields.email,
                UserFields.phone,
            )
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `updateUser - must return the updated user with a null e-mail`() {
        val userUpdateDTOMock = generateUserUpdateDTO(
            fullName = "Roberto",
            birthDate = LocalDate.of(1998, 10, 2),
            phone = "987789191"
        )
        val userMock = User.randomize()
        val updatedUserMock = userMock.withUpdatedValues(userUpdateDTOMock)

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns updatedUserMock

        userServiceMock.updateUser(userMock.id!!, userUpdateDTOMock).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertUserUpdate(
                original = userMock,
                updated = updatedUserMock,
                UserFields.fullName,
                UserFields.birthDate,
                UserFields.phone,
            )
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
    fun `updateUser - must throw NullPayloadException`() {
        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns mockk()
        every { userRepositoryMock.save(any()) } returns mockk()

        assertThrows<NullPayloadException> { userServiceMock.updateUser(UUID.randomUUID(), null) }

        verify(exactly = 0) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `partiallyUpdateUser - must return the updated user`() {
        val userPartialUpdateDTOMock = generateUserPartialUpdateDTO(
            fullName = "Roberto",
            birthDate = LocalDate.of(1998, 10, 2),
            email = "another@email.com",
            phone = "987789191"
        )
        val userMock = User.randomize()
        val updatedUserMock = userMock.withUpdatedValues(userPartialUpdateDTOMock)

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns updatedUserMock

        userServiceMock.partiallyUpdateUser(userMock.id!!, userPartialUpdateDTOMock).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertUserUpdate(
                original = userMock,
                updated = updatedUserMock,
                UserFields.fullName,
                UserFields.birthDate,
                UserFields.email,
                UserFields.phone,
            )
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `partiallyUpdateUser - must return the updated user with the same previous e-mail`() {
        val userPartialUpdateDTOMock = generateUserPartialUpdateDTO(
            fullName = "Roberto",
            birthDate = LocalDate.of(1998, 10, 2),
            phone = "987789191"
        )
        val userMock = User.randomize()
        val updatedUserMock = userMock.withUpdatedValues(userPartialUpdateDTOMock)

        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns updatedUserMock

        userServiceMock.partiallyUpdateUser(userMock.id!!, userPartialUpdateDTOMock).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.assertUserUpdate(
                original = userMock,
                updated = updatedUserMock,
                UserFields.fullName,
                UserFields.birthDate,
                UserFields.phone,
            )
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
    fun `partiallyUpdateUser - must throw NullPayloadException`() {
        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(id = any()) } returns mockk()
        every { userRepositoryMock.save(any()) } returns mockk()

        assertThrows<NullPayloadException> { userServiceMock.updateUser(UUID.randomUUID(), null) }

        verify(exactly = 0) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `deleteUser - must return the deleted user`() {
        val userMock = User.randomize()
        val deletedUserMock = userMock.asDeleted()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) } returns userMock
        every { userRepositoryMock.save(any()) } returns deletedUserMock

        userServiceMock.deleteUser(userMock.id!!).let { responseEntity ->
            assertEquals(HttpStatus.OK.value(), responseEntity.statusCodeValue)
            responseEntity.body?.let { responseBody ->
                responseBody.assertReturnedUser(user = userMock)
                assertNotNull(deletedUserMock.deletedAt)
            }
        }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 1) { userRepositoryMock.save(any()) }
    }

    @Test
    fun `deleteUser - must throw UserNotFoundException`() {
        val userMock = User.randomize()

        every { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) } returns null

        assertThrows<UserNotFoundException> { userServiceMock.deleteUser(userMock.id!!) }

        verify(exactly = 1) { userRepositoryMock.findFirstByIdAndDeletedAtIsNull(any()) }
        verify(exactly = 0) { userRepositoryMock.save(any()) }
    }

    private fun UserService.searchUsersWithDefaults(
        fullName: String? = null,
        cpf: String? = null,
        page: Int = 0,
        size: Int = 20,
        sort: UserFields = UserFields.fullName,
        direction: Direction = Direction.asc
    ) = this.searchUsers(
        fullName = fullName,
        cpf = cpf,
        page = page,
        size = size,
        sort = sort,
        direction = direction
    )

}