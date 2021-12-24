package dev.erickvieira.ppcc.service.user.domain.exception

import dev.erickvieira.ppcc.service.user.web.api.model.ApiErrorType

class InvalidPayloadException(payload: Any?) :
    BadRequestException(message = "Invalid payload: $payload", type = ApiErrorType.INVALID_PAYLOAD)