package dev.erickvieira.ppcc.service.user.domain.extension

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.web.api.model.UserDTO
import dev.erickvieira.ppcc.service.user.web.api.model.UserPartialUpdateDTO
import dev.erickvieira.ppcc.service.user.web.api.model.UserUpdateDTO
import java.time.OffsetDateTime

fun User.toUserDTO(): UserDTO = UserDTO()
    .id(id)
    .cpf(cpf)
    .name(name)
    .birthDate(birthDate)
    .phone(phone)
    .email(email)
    .createdAt(createdAt)
    .updatedAt(updatedAt)

fun User.withUpdatedValues(values: UserPartialUpdateDTO) = User(
    id = id,
    cpf = cpf,
    name = values.name?.ifBlank { name } ?: name,
    birthDate = values.birthDate ?: birthDate,
    phone = values.phone?.ifBlank { phone } ?: phone,
    email = values.email?.ifBlank { email } ?: email,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now()
)

fun User.withUpdatedValues(values: UserUpdateDTO) = User(
    id = id,
    cpf = cpf,
    name = values.name,
    birthDate = values.birthDate,
    phone = values.phone,
    email = values.email,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now()
)

fun User.asDeleted() = User(
    id = id,
    cpf = cpf,
    name = name,
    birthDate = birthDate,
    phone = phone,
    email = email,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = OffsetDateTime.now()
)