package dev.erickvieira.ppcc.service.user.domain.extension

import dev.erickvieira.ppcc.service.user.domain.entity.User
import dev.erickvieira.ppcc.service.user.web.api.model.UserCreationDTO
import java.time.OffsetDateTime

fun UserCreationDTO.toUser() = User(
    cpf = cpf,
    name = name,
    birthDate = birthDate,
    phone = phone,
    email = email,
    createdAt = OffsetDateTime.now()
)