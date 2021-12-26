package dev.erickvieira.ppcc.service.user.unit

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.web.api.model.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

@ActiveProfiles("test")
open class UserUnitTests {

    private val defaultFullName = "Erick"
    private val defaultBirthDate: LocalDate = LocalDate.of(2001, 1, 1)
    private val defaultCreatedAt: OffsetDateTime = OffsetDateTime.now().minusHours(1)

    protected fun generateUserCreationDTO(
        cpf: String = generateRandomCPF(),
        fullName: String = defaultFullName,
        birthDate: LocalDate = defaultBirthDate,
        email: String? = null,
        phone: String? = null
    ) = UserCreationDTO(
        cpf = cpf,
        fullName = fullName,
        birthDate = birthDate,
        email = email,
        phone = phone,
    )

    protected fun generateUserPartialUpdateDTO(
        fullName: String = defaultFullName,
        birthDate: LocalDate? = defaultBirthDate,
        email: String? = null,
        phone: String? = null
    ) = UserPartialUpdateDTO(
        fullName = fullName,
        birthDate = birthDate,
        email = email,
        phone = phone,
    )

    protected fun generateUserUpdateDTO(
        fullName: String = defaultFullName,
        birthDate: LocalDate = defaultBirthDate,
        email: String? = null,
        phone: String? = null
    ) = UserUpdateDTO(
        fullName = fullName,
        birthDate = birthDate,
        email = email,
        phone = phone,
    )

    protected fun User.Companion.randomize(
        id: UUID? = UUID.randomUUID(),
        cpf: String = generateRandomCPF(),
        fullName: String = defaultFullName,
        birthDate: LocalDate? = defaultBirthDate,
        email: String? = null,
        phone: String? = null,
        createdAt: OffsetDateTime? = defaultCreatedAt,
        updatedAt: OffsetDateTime? = null,
        deletedAt: OffsetDateTime? = null
    ) = User(
        id = id,
        cpf = cpf,
        fullName = fullName,
        birthDate = birthDate,
        email = email,
        phone = phone,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )

    protected fun UserCreationDTO.asSavedUser() = User(
        id = UUID.randomUUID(),
        cpf = cpf,
        fullName = fullName,
        birthDate = birthDate,
        phone = phone,
        email = email,
        createdAt = OffsetDateTime.now()
    )

    protected fun generateUserPage(
        number: Int = 0,
        size: Int = 20,
        content: List<User> = listOf(User.randomize()),
    ): Page<User> = PageImpl(content, PageRequest.of(number, size), content.size.toLong())

    protected fun generateEmptyUserPage() = generateUserPage(content = listOf())

    private fun generateRandomCPF() = (100000000..99999999999).random().toString().padStart(11, '0')

    protected fun PageUserDTO.assertPagination(page: Page<User>) {
        assertEquals(page.pageable.sort.toString(), sortedBy)
        assertEquals(page.totalPages, pageCount)
        assertEquals(page.totalElements, total)
        assertEquals(page.numberOfElements, content.size)
    }

    protected fun UserDTO.assertUserCreation(input: UserCreationDTO, scapeIdNotNullAssert: Boolean = false) {
        if (!scapeIdNotNullAssert) assertNotNull(id)
        assertEquals(input.fullName, fullName)
        assertEquals(input.birthDate, birthDate)
        assertEquals(input.email, email)
        assertEquals(input.phone, phone)
        assertNotNull(createdAt)
        assertNull(updatedAt)
    }

    protected fun UserDTO.assertReturnedUser(user: User) {
        assertNotNull(id)
        assertNotNull(user.id)
        assertEquals(user.id, id)
        assertEquals(user.fullName, fullName)
        assertEquals(user.cpf, cpf)
        assertEquals(user.birthDate, birthDate)
        assertEquals(user.email, email)
        assertEquals(user.phone, phone)
        assertEquals(user.createdAt, createdAt)
    }

    protected fun UserDTO.assertUserUpdate(original: User, updated: User, vararg changes: UserFields) {
        assertEquals(original.id, id)
        assertEquals(updated.id, id)
        assertEquals(original.cpf, cpf)
        assertEquals(updated.cpf, cpf)
        assertEquals(updated.fullName, fullName)
        original.let {
            (if (changes.contains(UserFields.fullName)) assertNotEquals(
                it.fullName,
                fullName
            ) else assertEquals(it.fullName, fullName))
        }
        assertEquals(updated.birthDate, birthDate)
        original.let {
            (if (changes.contains(UserFields.birthDate)) assertNotEquals(
                it.birthDate,
                birthDate
            ) else assertEquals(it.birthDate, birthDate))
        }
        assertEquals(updated.email, email)
        original.let {
            (if (changes.contains(UserFields.email)) assertNotEquals(
                it.email,
                email
            ) else assertEquals(it.email, email))
        }
        assertEquals(updated.phone, phone)
        original.let {
            (if (changes.contains(UserFields.phone)) assertNotEquals(
                it.phone,
                phone
            ) else assertEquals(it.phone, phone))
        }
        assertNotNull(updatedAt)
    }

}
