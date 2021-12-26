package dev.erickvieira.ppcc.service.user.domain.extension

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.web.api.model.UserCreationDTO
import dev.erickvieira.ppcc.service.user.web.api.model.UserDTO
import dev.erickvieira.ppcc.service.user.web.api.model.UserPartialUpdateDTO
import dev.erickvieira.ppcc.service.user.web.api.model.UserUpdateDTO
import java.time.OffsetDateTime

fun User.toUserDTO() = UserDTO(
    id = id!!,
    cpf = cpf,
    fullName = fullName,
    birthDate = birthDate!!,
    phone = phone,
    email = email,
    createdAt = createdAt!!,
    updatedAt = updatedAt,
)

fun User.Companion.fromUserCreationDTO(input: UserCreationDTO) = User(
    cpf = input.cpf,
    fullName = input.fullName,
    birthDate = input.birthDate,
    phone = input.phone,
    email = input.email,
    createdAt = OffsetDateTime.now()
)

fun User.withUpdatedValues(values: UserPartialUpdateDTO) = User(
    id = id,
    cpf = cpf,
    fullName = values.fullName?.ifBlank { fullName } ?: fullName,
    birthDate = values.birthDate ?: birthDate,
    phone = values.phone?.ifBlank { phone } ?: phone,
    email = values.email?.ifBlank { email } ?: email,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now()
)

fun User.withUpdatedValues(values: UserUpdateDTO) = User(
    id = id,
    cpf = cpf,
    fullName = values.fullName,
    birthDate = values.birthDate,
    phone = values.phone,
    email = values.email,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now()
)

fun User.asDeleted() = User(
    id = id,
    cpf = cpf,
    fullName = fullName,
    birthDate = birthDate,
    phone = phone,
    email = email,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = OffsetDateTime.now()
)