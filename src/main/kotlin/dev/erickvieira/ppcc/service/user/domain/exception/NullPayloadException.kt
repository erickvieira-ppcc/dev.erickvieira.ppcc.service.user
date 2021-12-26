package dev.erickvieira.ppcc.service.user.domain.exception

import dev.erickvieira.ppcc.service.user.web.api.model.ApiErrorType

class NullPayloadException(payload: String? = null) : BadRequestException(
    message = "The${payload?.let { it.ifBlank { " " } } ?: " $payload "}payload can't be null",
    type = ApiErrorType.nullPayload
)