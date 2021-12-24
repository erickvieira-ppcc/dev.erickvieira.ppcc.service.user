package dev.erickvieira.ppcc.service.user.domain.exception

import dev.erickvieira.ppcc.service.user.web.api.model.ApiErrorType

open class UnexpectedException(message: String?) : BaseException(
    message = message ?: "Unexpected Error",
    type = ApiErrorType.UNEXPECTED_ERROR
)
