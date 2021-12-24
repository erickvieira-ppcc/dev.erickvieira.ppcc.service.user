package dev.erickvieira.ppcc.service.user.domain.exception

import dev.erickvieira.ppcc.service.user.web.api.model.ApiErrorType

class InvalidCpfException(cpf: String?) : BadRequestException(
    message = "The given CPF is invalid: $cpf",
    type = ApiErrorType.INVALID_CPF
)