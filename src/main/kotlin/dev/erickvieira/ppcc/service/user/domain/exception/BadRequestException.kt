package dev.erickvieira.ppcc.service.user.domain.exception

import dev.erickvieira.ppcc.service.user.web.api.model.ApiErrorType

open class BadRequestException(message: String, type: ApiErrorType) : BaseException(message, type)
