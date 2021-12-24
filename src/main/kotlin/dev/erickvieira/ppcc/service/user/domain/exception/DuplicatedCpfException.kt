package dev.erickvieira.ppcc.service.user.domain.exception

import dev.erickvieira.ppcc.service.user.web.api.model.ApiErrorType

class DuplicatedCpfException(cpf: String) : ConflictException(
    message = "The CPF $cpf is already registered",
    type = ApiErrorType.DUPLICATED_CPF
)