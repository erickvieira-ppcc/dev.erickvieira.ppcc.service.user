package dev.erickvieira.ppcc.service.user

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.web.api.model.UserCreationDTO
import dev.erickvieira.ppcc.service.user.web.api.model.UserPartialUpdateDTO
import dev.erickvieira.ppcc.service.user.web.api.model.UserUpdateDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

open class UserUnitTests {

    protected open val defaultName = "Erick"
    protected open val defaultBirthDate: LocalDate = LocalDate.of(2001, 1, 1)
    protected open val defaultCreatedAt: OffsetDateTime = OffsetDateTime.now().minusHours(1)

    protected fun generateUserCreationDTO(
        cpf: String = generateRandomCPF(),
        name: String = defaultName,
        birthDate: LocalDate? = defaultBirthDate,
        email: String? = null,
        phone: String? = null
    ): UserCreationDTO = UserCreationDTO()
        .cpf(cpf)
        .name(name)
        .birthDate(birthDate)
        .email(email)
        .phone(phone)

    protected fun generateUserPartialUpdateDTO(
        name: String = defaultName,
        birthDate: LocalDate? = defaultBirthDate,
        email: String? = null,
        phone: String? = null
    ): UserPartialUpdateDTO = UserPartialUpdateDTO()
        .name(name)
        .birthDate(birthDate)
        .email(email)
        .phone(phone)

    protected fun generateUserUpdateDTO(
        name: String = defaultName,
        birthDate: LocalDate? = defaultBirthDate,
        email: String? = null,
        phone: String? = null
    ): UserUpdateDTO = UserUpdateDTO()
        .name(name)
        .birthDate(birthDate)
        .email(email)
        .phone(phone)

    protected fun generateUser(
        id: UUID? = UUID.randomUUID(),
        cpf: String = generateRandomCPF(),
        name: String = defaultName,
        birthDate: LocalDate? = defaultBirthDate,
        email: String? = null,
        phone: String? = null,
        createdAt: OffsetDateTime? = defaultCreatedAt,
        updatedAt: OffsetDateTime? = null,
        deletedAt: OffsetDateTime? = null
    ) = User(
        id = id,
        cpf = cpf,
        name = name,
        birthDate = birthDate,
        email = email,
        phone = phone,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )

    protected fun generateUserPage(
        number: Int = 0,
        size: Int = 20,
        content: List<User> = listOf(generateUser()),
    ): Page<User> = PageImpl(content, PageRequest.of(number, size), content.size.toLong())

    protected fun generateEmptyUserPage() = generateUserPage(content = listOf())

    private fun generateRandomCPF() = (100000000..99999999999).random().toString().padStart(11, '0')

}
